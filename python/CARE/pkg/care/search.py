# Causality Reasoning
# @author: Henry Su 2014

################################################################################

try:
	# Python 2.7+
	from Queue import *
except ImportError:
	# Python 3+
	from queue import *

from copy import deepcopy

from ..l2p.l2pcoll import *
from ..l2p.l2pgraph import *
from ..l2p.l2pmath import *
from ..l2p.l2pstat import *

################################################################################

# This class runs the coverage-directed search on the onset to generate a set of
# implicants. The algorithm is different from the original BOOM as it does not
# run multiple iterations, but rather, exhaustively searches for all possible
# permutations. Depending on the quality of the heuristics, the number of
# ties may be smaller or larger, which will affect the quality and runtime of
# this algorithm.
class CDSearch:
	# @param ratios: Ratios for influence of each heuristic.
	#	NOTE: ordered as lf, degree, betweenness, subset degree
	# @opt_param resolution: Adjusts the precisions, and therefore 
	#	distinction/ similarity of literals
	def __init__(self, onset, offset, ratios, resolution = 0.00001, logger = None):
		if len(ratios) < 4:
			raise IndexError
		self.logger = logger					# logger object to log messages
		self.onset = onset						# onset
		self.offset = offset					# offset
		self.ratios = list(ratios)				# ratios for each heuristic
		self.resolution = resolution			# precision of literal scores
		self.num_vars = len(onset[0])			# number of variables
		# calculate degree and betweenness once initially
		self.degree, self.betweenness = self.get_centralities(onset, offset)

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 

	# An efficient and determinitic coverage-directed search.
	def cd_search(self):
		self.log('Starting the Coverage-Directed Search...')
		implicants = []
		# all permutations of the onset overall covering; only need to remember
		# minterm row indexes
		# initial permutation is a completely uncovered onset
		uncovered_permutations = [[i for i in range(len(self.onset))]]
		i = 0
		while (i < len(uncovered_permutations)):
			self.cd_search_helper(uncovered_permutations, uncovered_permutations[i], implicants)
			i += 1
		self.log('Finished the Coverage-Directed Search.')
		return implicants

	# Recursively find a set of implicants which wholly covers the onset.
	def cd_search_helper(self, uncovered_permutations, current_permutation, implicants):
		# stopping condition when all minterms are covered
		if current_permutation:
			# all permutations of the next implicant
			implicant_permutations = self.construct_implicant(current_permutation)
			# NOTE: a term/ implicant is represented as a list of literals
			# NOTE: a literal is represented as a tuple = (variable index, form)
			for implicant in implicant_permutations:
				# if the new implicant has not been seen before
				if implicant not in implicants:
					implicants.append(implicant)
				# find the remaining uncovered minterms
				# NOTE: This step is performed regardless of whether the implicant
				# was seen before or not.
				# NOTE: Ordering of the row indexes must be preserved. This is necessary
				# for identifying unique permutations of the covering.
				new_uncovered_permutation = [i for i in current_permutation \
					if any(self.onset[i][literal[0]] != literal[1] for literal in implicant)]
				# if the new permutation has not been seen before
				if new_uncovered_permutation not in uncovered_permutations:
					uncovered_permutations.append(new_uncovered_permutation)
				else:
					self.log('!Duplicate overall uncovered onset permutation found and discarded.')

	# Returns all equally best permutations of the next implicant.
	def construct_implicant(self, current_permutation):
		implicant_permutations = []
		self.construct_implicant_helper(implicant_permutations, [], 
			current_permutation, [i for i in range(len(self.offset))])
		# sort and remove duplicates
		for implicant in implicant_permutations:
			implicant.sort(key = lambda x: (x[0], x[1]))
		return [list(implicant) \
			for implicant in set(tuple(implicant) for implicant in implicant_permutations)]

	# Recursively finds literals to add to the implicant until it no longer
	# intersect with the offset.
	# @param term: the current term in construction
	# @param current_covering: row indexes of onset minterms covered by the current term
	# @param current_intersect: row indexes of offset minterms that the current term intersects with
	def construct_implicant_helper(self, implicant_permutations, term, current_covering, current_intersect):
		# stopping condition when the term no longer intersects with the offset
		if current_intersect:
			# all next best literal candidates to add to the term
			candidates = self.literal_candidates(current_covering, term)
			for candidate in candidates:
				new_term = deepcopy(term)
				new_term.append(candidate)
				new_current_covering = [i for i in current_covering \
					if self.onset[i][candidate[0]] == candidate[1]]
				# special case if the new covering is empty; revert changes
				if not new_current_covering:
					new_current_covering = deepcopy(current_covering)
				new_current_intersect = [i for i in current_intersect \
					if self.offset[i][candidate[0]] == candidate[1]]
				# recursively run the helper function to complete the implicant
				self.construct_implicant_helper(implicant_permutations, new_term, 
					new_current_covering, new_current_intersect)
		else:
			# NOTE: deal with duplicates later
			implicant_permutations.append(term)

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 

	# Returns all next best literal candidates.
	def literal_candidates(self, current_covering, term):
		# literal frequencies
		lf = self.get_lf(current_covering)
		# degree centrality
		deg = self.degree
		# betweenness centrality
		bet = self.betweenness
		# subset degree centrality
		sub = self.get_sub_degree(term)
		# calculate the overall score for each literal, using the ratios
		overall_score = {literal: self.ratios[0]*lf[literal]+\
			self.ratios[1]*deg[literal[0]]+self.ratios[2]*bet[literal[0]]+\
			self.ratios[3]*sub[literal[0]] for literal in lf.keys()}
		# rounding overall score to form groupings between similar literals, and
		# thereby increasing the chances of a tie
		for literal in overall_score.keys():
			# round to a user defined resolution
			overall_score[literal] = round_resolution(overall_score[literal], self.resolution)
		# variables already in the current term
		used_variables = [literal[0] for literal in term]
		# best literal candidates
		candidates = []
		covered_variables = [literal[0] for literal in term]
		max_score = 0
		for literal, score in list(overall_score.items()):
			# variable not already used in term
			if literal[0] not in covered_variables:
				if score > max_score:
					candidates = [literal]
					max_score = score
				elif score == max_score:
					candidates.append(literal)
		# if there is a tie between a normal literal and its complimented form,
		# the normal literal is preferred.
		# marked literals are deleted together at the end
		marked_literals = []
		for i in range(len(candidates)):
			# if compliment literal
			if not candidates[i][1]:
				# if the normal literal also exists amongst the candidates
				if (candidates[i][0], 1) in candidates:
					marked_literals.append(i)
		multi_delete(candidates, marked_literals)
		# if there are multiple candidates
		if len(candidates) > 1:
			self.log('!Multiple candidate literals for implicant construction:\n'+str(candidates))
		return candidates

################################################################################

	# Returns the literal frequencies of each literal. This function is used dynamically,
	# when constructing implicants.
	# @param current_covering: minterms not covered by the current term
	def get_lf(self, current_covering, normalize = True):
		lf = {}
		for j in range(self.num_vars):
			# initializing counts
			lf[(j, 1)] = 0 				# normal literal
			lf[(j, 0)] = 0 				# complimented form
			# count literal frequencies
			for i in current_covering:
				if self.onset[i][j]:
					lf[(j, 1)] += 1
				else:
					lf[(j, 0)] += 1
		# normalized
		return rescale(lf)

	# Returns the degree and betweenness centralities of each variable.
	def get_centralities(self, onset, offset):
		self.log('!Generating network graphs...')
		onset_matrix = self.get_adjacency_matrix(onset)
		offset_matrix = self.get_adjacency_matrix(offset)
		# NOTE: the normal graphs are also used for calculating the subset degree
		self.onset_graph = Graph().from_adjacency_matrix(onset_matrix)
		self.offset_graph = Graph().from_adjacency_matrix(offset_matrix)
		invert_onset_graph = Graph().from_adjacency_matrix(onset_matrix, invert = True)
		invert_offset_graph = Graph().from_adjacency_matrix(offset_matrix, invert = True)
		self.log('!Calculating degree and betweenness centralities...')
		onset_degree = self.onset_graph.degree_centrality(normalize = False)
		offset_degree = self.offset_graph.degree_centrality(normalize = False)
		onset_betweenness = invert_onset_graph.betweenness_centrality(normalize = False)
		offset_betweenness = invert_offset_graph.betweenness_centrality(normalize = False)
		self.log('!Done!')
		return rescale({i: onset_degree[i]-offset_degree[i] for i in range(self.num_vars)}), \
			rescale({i: onset_betweenness[i]-offset_betweenness[i] for i in range(self.num_vars)})

	# Returns the number of unique links (in the network graph) that each
	# variable shares with the current term.
	def get_sub_degree(self, term):
		subset = [literal[0] for literal in term]
		onset_sub_deg = self.onset_graph.sub_degree_centrality(subset, normalize = False)
		offset_sub_deg = self.offset_graph.sub_degree_centrality(subset, normalize = False)
		return rescale({node_id: onset_sub_deg[node_id]-offset_sub_deg[node_id] \
			for node_id in onset_sub_deg.keys()})

	# Creates an adacency matrix from a dataset of binary ints. Each column forms
	# a node in the network graph, and each pair of nodes that exists in a row
	# increments the corresponding edge's weight.
	# NOTE: This produces an adjacency matrix of an undirected graph; only half
	# of the matrix is generated.
	def get_adjacency_matrix(self, dataset, density = 0.25):
		num_vars = len(dataset[0])
		adjacency_matrix = [[0 for i in range(num_vars)] for j in range(num_vars)]
		for row in dataset:
			# for every pair of nodes which exists in the matrix
			for i in range(num_vars):
				if row[i]:
					for j in range(i+1, num_vars):
						if row[j]:
							adjacency_matrix[i][j] += 1
		return self.reduced_density(adjacency_matrix, density)

	# Returns a reduced version of the adjacency matrix. This function guarantees
	# that the resulting network graph would have no more than a specified
	# edge density.
	def reduced_density(self, matrix, density):
		num_vars = len(matrix)
		# find cutoff threshold value
		values = []
		for i in range(len(matrix)):
			for j in range(i, len(matrix)):
				values.append(matrix[i][j])
		threshold = sorted(values, reverse = True)[int((len(values)-1)*density)]
		reduced_adjacency_matrix = [[0 for i in range(num_vars)] for j in range(num_vars)]
		for i in range(num_vars):
			for j in range(num_vars):
				# only edge values greater than the threshold
				if matrix[i][j] > threshold:
					reduced_adjacency_matrix[i][j] = matrix[i][j]
		return reduced_adjacency_matrix

################################################################################

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
