

from ..l2p.l2pstat import rescale


# Original BOOM (Fiser & Hlavicka, 2002) and improved CANAL (Su, 2013).
# Default 2 iterations.
# Option to enable/ disable social values (sv).
# Option to choose between LCMC (0) or WLWO (1) heuristic.
class Boom:
	def __init__(self, iterations = 2, use_sv = True, heuristic = 1, logger = None):
		pass

	# CD Search
	def cd_search(self, onset = None, ):

	# Implicant Expansion
	def implicant_expansions(self):

	# Solution to Covering Problem
	def cover_solution(self):


	# Returns a mapping of every causal variable and its social value, which
	# is essentially its degree centrality.
	# NOTE: This method only considers the onset, and ignores the offset.
	def social_value(self, onset, normalized = True):
		# Generate an adjacency matrix of the onset.
		onset_AM = self.adjacency_matrix(onset)
		# The degree centrality is simply the number of non-zero values
		# in each row/ column.
		deg = {i: self.count_non_zero(onset_AM[i])+\
			self.count_non_zero([onset_AM[j][i] for j in range(num_vars)])\
			for i in range(num_vars)}
		# normalized
		if normalized:
			return rescale(deg)
		else:
			return deg


	# Creates a weighted adacency matrix. Each causal variable corresponds to a node in
	# the network graph, and each pair of variables that exists in a configuration
	# leading to the desirable outcome, increments the corresponding edge's weight.
	# NOTE: This method produces a matrix for an undirected graph; 
	# only half of the matrix is generated.
	def weighted_adjacency_matrix(self, dataset):
		num_vars = len(dataset[0])
		AM = [[0 for i in range(num_vars)] for j in range(num_vars)]
		for row in dataset:
			# for every pair of causal variables
			for i in range(num_vars):
				if row[i]:
					for j in range(i+1, num_vars):
						# if both causal variables exists in a configuration
						if row[j]:
							AM[i][j] += 1
		return AM

	# Counts number of non-zero elements in a list.
	def count_non_zero(self, list_):
		count = 0
		for i in range(len(list_)):
			if list_[i]:
				count += 1
		return count


	# Returns the literal frequencies of each literal.
	def literal_frequencies(self, onset, current_covering, normalized = True):
		lf = {}
		for v in range(len(onset[0])):
			# initializing counts
			lf[(v, 1)] = 0 				# normal literal
			lf[(v, 0)] = 0 				# complimented form
			# count literal frequencies
			for m in current_covering:
				if onset[m][v]:
					lf[(v, 1)] += 1
				else:
					lf[(v, 0)] += 1
		# normalized
		if normalized:
			return rescale(lf)
		else:
			return lf