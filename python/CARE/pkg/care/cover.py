# Causality Analysis
# @author: Henry Su 2014

################################################################################

from ..l2p.l2pstat import *

################################################################################

# This class finds the solution to the Unate Covering problem using a heuristic
# approach. Given a set of prime implicants, the algorithm finds the minimal set
# of prime implicants which covers the entire onset, that is the set of
# essential prime implicants.
class UnateCover:
	def __init__(self, prime_implicants, onset, logger = None):
		self.logger = logger
		self.prime_implicants = prime_implicants
		self.onset = onset

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 

	# Runs the algorithm to find the solution to the covering problem.
	def unate_cover(self):
		# create a cover matrix, which is necessary for one of the heuristics
		return self.unate_cover_helper(self.get_cover_matrix())

	# This function generates the cover matrix, which is used in the
	# covering algorithm.
	# The cover matrix summarises which minterms each prime implicant covers.
	# For each minterm of the onset, there is a corresponding row in the cover matrix.
	# For each prime implicant, there is a corresponding column in the cover matrix.
	def get_cover_matrix(self):
		return [[1 if all(minterm[literal[0]] == literal[1] for literal in prime_implicant) else 0 \
			for prime_implicant in self.prime_implicants] for minterm in self.onset]

	# This function controls which heuristics are used, and how they are ordered.
	def unate_cover_helper(self, cover_matrix):
		# weighted literal, weighted output heuristics
		wlwo = self.get_wlwo()
		# prime implicant length which is the number of literals
		lengths = self.get_lengths()
		# essential prime implicants
		essentials = []
		# while the cover matrix is not empty, i.e. not all minterms of the onset are covered
		while cover_matrix != []:
			# Set of heuristics for the selection criteria for each literal of the implicant.
			# The selection_criteria has the format:
			# [[literal_index, heuristic_1_score, heuristic_2_score, ...]...]
			selection_criteria = [[i] for i in range(len(self.prime_implicants))]
			# cover_potential for each prime_implicant,
			# which is the number of minterms each prime implicant covers
			cover_potential = self.get_cover_potential(cover_matrix)
			# calculate heursitics
			for i in range(len(self.prime_implicants)):
				selection_criteria[i].append(cover_potential[i])
				selection_criteria[i].append(lengths[i])
				selection_criteria[i].append(wlwo[i])
				# NOTE: More heuristics can simply be added to reduce the chances of a tie.
			# sort the selection criteria
			best_pi = sorted(selection_criteria, 
				key = lambda x : (-x[1], x[2], -x[3]))[0][0]
			# store the essential prime implicant
			essentials.append(self.prime_implicants[best_pi])
			# update the cover matrix
			cover_matrix = [row for row in cover_matrix if not row[best_pi]]
		return essentials

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 

	# Returns a list of cover_potential counts. This list has the same order of the
	# prime implicants as the cover_matrix.
	def get_cover_potential(self, cover_matrix):
		# number of minterms covered by each prime implicant
		cover_potential = []
		# for each prime implicant
		for j in range(len(cover_matrix[0])):
			# count the number of minterms covered by each prime implicant
			# NOTE: the minterms have not been covered by other EPIs already
			cover_potential.append([cover_matrix[i][j] for i in range(len(cover_matrix))].count(1))
		return cover_potential

	# Returns the weighted literal, weighted output heuristic of each literal from
	# each prime implicant from all prime implicants.
	# The WLWO heuristic is originally developed by Kagliwal et al. (2012)
	def get_wlwo(self):
		literal_weights = self.get_literal_weights()
		# WLWO = WL*WO
		# WL = sum of all literal weights for each literal in the prime implicant
		# WO = sum of the weight of output containing the prime implicant;
		# in this case, WO = size of the onset as we use single output functions
		return [sum([literal_weights[literal] for literal in prime_implicant])*len(self.onset) \
			for prime_implicant in self.prime_implicants]

	# Returns a dictionary of literal weights for each literal. Essentially counts
	# the number of occurrences of a literal amongst all prime implicants.
	def get_literal_weights(self):
		literal_weights = {}
		for prime_implicant in self.prime_implicants:
			for literal in prime_implicant:
				if literal not in literal_weights.keys():
					literal_weights[literal] = 1
				else:
					literal_weights[literal] += 1
		return literal_weights

	# Returns the prime implicant length, which is number of literals, for each prime implicant.
	# NOTE: The lengths need to inversed because the shortest length is preferred.
	def get_lengths(self):
		return [len(prime_implicant) for prime_implicant in self.prime_implicants]

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 

	# Helper function for logging messages.
	def log(self, message = '', matrix = [], char_limit = 240, line_limit = 256):
		# NOTE: Special case when the message begins with a '!'. This message is
		# used for debugging and testing purposes.
		if str(message[0]) == '!':
			print(message[1:])
			for row in matrix:
				print(','.join([str(x) for x in row]))
		# log message
		elif self.logger:
			self.logger.log(message, matrix, char_limit, line_limit)

################################################################################
