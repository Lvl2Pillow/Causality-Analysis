# Causality Reasoning
# @author: Henry Su 2014

################################################################################

from copy import deepcopy
from csv import reader
from os.path import isfile, getsize
from re import search

from ..l2p.l2pcoll import *
from ..l2p.l2pstat import *

################################################################################

# The Data class provides functions to read an input dataset from a CSV file
# and manipulate its contents.
# - Data validation
# - Find variable names, onset, and offset
# - Calibration: Fuzzy to rough crisp
# - Data cleansing
class Data:
	def __init__(self, filename, header = True, logger = None):
		self.filename = filename	# filename
		self.header = header		# whether dataset contains a header
		self.logger = logger		# optional logger object
		self.variables = []			# variable names
		self.onset = []				# minterms leading to positive outcome
		self.offset = []			# minterms leading to negative outcome
		self.is_crisp = True		# whether dataset is crisp or fuzzy

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 

	# Validation checks on the input file.
	def is_valid(self):
		self.log('Validating input file.')
		filename = self.filename
		header = self.header
		try:
			# file exists
			if not isfile(filename):
				self.log('!Error! File not found.')
				return False
			# empty file
			if not getsize(filename):
				self.log('Error! File is empty.')
				return False
			# read through file
			num_cols = []
			toggle = header
			with open(filename) as csv_file:
				for row in reader(csv_file, delimiter = ','):
					# non-empty line
					if row:
						# blank value
						if any(not x for x in row):
							self.log('Error! Values must be non-empty.')
							return False
						# count number of columns
						num_cols.append(len(row))
						# header
						if toggle:
							# header contains only alphanumeric characters or ' ', '.', '_', '-'
							if any(search('[^a-zA-Z0-9\s\._-]', x) for x in row):
								self.log('Warning! Variable names must be alphanumeric.')
								return False
							# unique variable names
							if len(row) != len(set(row)):
								self.log('Error! Variable names must be unique.')
								return False
							# toggle off header
							toggle = False
						else:
							# values within the interval [0, 1]
							if any(float(x) < 0 or float(x) > 1 for x in row):
								self.log('Error! Values must be within the interval [0, 1].')
								return False
							# outcome must be 0 or 1
							if row[-1].strip() not in ['0', '1']:
								self.log('Error! Outcomes must be 0 or 1.')
								return False
			# rows have same number of columns
			if len(set(num_cols)) != 1:
				self.log('Error! Rows must have equal length.')
				return False
			# all validation checks pass
			self.log('Input file has been validated!')
			return True
		except Exception:
			self.log('Error! Invalid file.')
			return False
		finally:
			csv_file.close()

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 

	# Reads a CSV file and generates a matrix representation of the dataset.
	# NOTE: The last column of the input dataset must be the outcome.
	def read(self):
		self.log('Reading input dataset from file.')
		filename = self.filename
		header = self.header
		try:
			toggle = header
			with open(filename) as csv_file:
				for row in reader(csv_file, delimiter = ','):
					# non-empty line
					if row:
						# header
						if toggle:
							# discard the 'outcome'
							self.variables = row[:-1]
							# toggle off header
							toggle = False
						elif int(row[-1]):
							self.onset.append([float(x) for x in row[:-1]])
						else:
							self.offset.append([float(x) for x in row[:-1]])
			# default variable names
			if not self.variables:
				self.variables = [str(i) for i in range(len(row))]
			# whether dataset is crisp of fuzzy
			self.is_crisp = not(any(any(x-int(x) > 0 for x in row) for row in self.onset) or \
				any(any(x-int(x) > 0 for x in row) for row in self.offset))
		except Exception:
			self.log('Error! Failed to read file.')
			self.variables = []
			self.onset = []
			self.offset = []
		finally:
			csv_file.close()
			return (self.variables, self.onset, self.offset)

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 

	def calibrate(self, stat = 'median'):
		self.log('Calibrating the dataset.')
		dataset = self.onset+self.offset
		num_rows = len(dataset)
		num_cols = len(dataset[0])
		# Calculate default thresholds
		means = []					# list of means for each column
		medians = []				# list of medians for each column
		for j in range(num_cols):
			col = [dataset[i][j] for i in range(num_rows)]
			medians.append(median(col))
			means.append(mean(col))
		# Calibrate
		for j in range(num_cols):
			if stat == 'median':
				for i in range(len(self.onset)):
					self.onset[i][j] = 1 if self.onset[i][j] > medians[j] else 0
				for i in range(len(self.offset)):
					self.offset[i][j] = 1 if self.offset[i][j] > medians[j] else 0
			elif stat == 'mean':
				for i in range(len(self.onset)):
					self.onset[i][j] = 1 if self.onset[i][j] > means[j] else 0
				for i in range(len(self.offset)):
					self.offset[i][j] = 1 if self.offset[i][j] > means[j] else 0
			else:
				self.log("Error! Oops, something went horribly wrong...")
				return (None, None)
		self.is_crisp = True
		self.log('Calibration completed.')
		return (self.onset, self.offset)

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 

	# Cleans the data. Identical configurations that exist in both the onset 
	# and offset are resolved. The set with the highest proportion is preferred.
	# If there is a tie, the configuration is removed completely.
	def clean(self):
		self.log('Cleaning data. Resolving conflicting minterms found in both '+\
			'the onset and offset.')
		num_onset = float(len(self.onset))		# number of minterms from the onset
		num_offset = float(len(self.offset))	# number of minterms from the offset
		# dictionary containing every unique minterm configuration (key), with
		# the row index(es) which they appeared in the dataset
		onset_configurations = composition([tuple(minterm) for minterm in self.onset])
		offset_configurations = composition([tuple(minterm) for minterm in self.offset])
		# intersecting minterm configurations are conflicting
		conflict_minterms = intersect(onset_configurations.keys(), offset_configurations.keys())
		# marked minterms are deleted together at the end
		marked_onset = []
		marked_offset = []
		# resolve conflicts
		for minterm in conflict_minterms:
			# proportion of minterm from onset and offset
			onset_proportion = len(onset_configurations[minterm])/num_onset
			offset_proportion = len(offset_configurations[minterm])/num_offset
			if onset_proportion > offset_proportion:
				for i in offset_configurations[minterm]:
					marked_offset.append(i)
			elif onset_proportion < offset_proportion:
				for i in onset_configurations[minterm]:
					marked_onset.append(i)
			# else tie
			else:
				for i in offset_configurations[minterm]:
					marked_offset.append(i)
				for i in onset_configurations[minterm]:
					marked_onset.append(i)
		# delete marked minterms
		if marked_onset:
			# self.log('!Removed from onset:', [self.onset[i] for i in marked_onset])
			self.log('Warning! The following minterms '+\
				'were removed from the onset due to conflicts:',
				[self.onset[i] for i in marked_onset])
		if marked_offset:
			# self.log('!Removed from offset:', [self.offset[i] for i in marked_offset])
			self.log('Warning! The following minterms '+\
				'were removed from the offset due to conflicts:',
				[self.offset[i] for i in marked_offset])
		# Remove all conflicts from the dataset.
		multi_delete(self.onset, marked_onset)
		multi_delete(self.offset, marked_offset)
		self.log('Cleansing completed.')
		return (self.variables, self.onset, self.offset)

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 

	# Helper function for logging messages.
	def log(self, message = '', matrix = [], line_wrap = 240, line_limit = 256):
		# NOTE: Special case when the message begins with a '!'. This message is
		# used for debugging and testing purposes.
		if str(message[0]) == '!':
			print(message[1:])
			for row in matrix:
				print(','.join([str(x) for x in row]))
		# log message
		elif self.logger:
			self.logger.log(message, matrix, line_wrap, line_limit)

################################################################################
