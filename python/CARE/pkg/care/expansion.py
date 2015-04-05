# Causality Reasoning
# @author: Henry Su 2014

################################################################################

from copy import deepcopy

################################################################################

# The implicant expansion finds the minimal form of an implicant, essentially
# finding the prime implicant.
# The method for finding the minimal form follows an exhaustive approach.
class ImplicantExpansion:
	# @param implicants: Set of implicants found from the coverage-directed search.
	def __init__(self, implicants, offset, degree, betweenness, logger = None):
		self.logger = logger
		self.implicants = implicants
		self.offset = offset
		self.degree = degree
		self.betweenness = betweenness

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 

	# Run the implicant expansion algorithm and returns the set of prime implicants.
	# NOTE: As implicants are already sorted from the CDSearch, there is no
	# need to sort again.
	def implicant_expansion(self):
		self.log('Starting the Implicant Expansion...')
		prime_implicants = []
		# find the minimal form for each implicant
		for implicant in self.implicants:
			# recursively reduce the implicant until it can no longer be reduced
			self.implicant_expansion_helper(prime_implicants, implicant)
		self.log('Finished the Implicant Expansion.')
		# return unique prime implicants
		return [list(prime_implicant) \
			for prime_implicant in set(tuple(prime_implicant) for prime_implicant in prime_implicants)]

	# Finds the minimal form(s) of an implicant recursively. The minimal form of an
	# implicant has the least possible literals without intersecting the offset.
	def implicant_expansion_helper(self, prime_implicants, term):
		# candidate literals for removal; if there are no candidates, then no
		# more removals can be made, at which stage, we have found a prime implicant
		candidates = self.literal_candidates(term)
		# check if multiple candidates
		if len(candidates) > 1:
			self.log('!Multiple candidate literals for prime implicant removal: '+str(candidates))
		if candidates:
			for candidate in candidates:
				new_term = deepcopy(term)
				new_term.remove(candidate)
				# recursively call self until no more removals can be made
				self.implicant_expansion_helper(prime_implicants, new_term)
		else:
			prime_implicants.append(term)

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 

	# Returns next best literal candidate for removal.
	def literal_candidates(self, term):
		candidates = []
		# Euclidean distance between a term and every minterm of the offset
		distances = [self.get_distance(term, minterm) for minterm in self.offset]
		# for each literal, the minimum difference with the offset after that literal
		# has been removed
		min_distances = [self.get_min_distance(distances, literal) for literal in term]
		# Set of heuristics for the selection criteria for each literal of the implicant.
		# The selection_criteria has the format:
		# [(literal_index, heuristic_1_score, heuristic_2_score, ...)...]
		selection_criteria = [(i, min_distances[i], 
			self.degree[term[i][0]], self.betweenness[term[i][0]]) for i in range(len(term))]
		# sort the selection criteria
		selection_criteria.sort(key = lambda x : (-x[1], x[2], x[3]))
		# if all min_distances = 0, no more removals can be made
		for i in range(len(selection_criteria)):
			# if equally best and min_distances != 0
			if selection_criteria[i][1:] == selection_criteria[0][1:] and \
				selection_criteria[i][1]:
				candidates.append(term[selection_criteria[i][0]])
		return candidates

	# Returns the Euclidean distance between a minterm and a term.
	def get_distance(self, term, minterm):
		distance = 0
		for literal in term:
			if minterm[literal[0]] != literal[1]:
				distance += 1
		return distance

	# Returns the min distance of each literal in the term. The min distance is 
	# the minimum Euclidean distance between the term-literal and any minterm from the offset.
	def get_min_distance(self, distances, literal):
		temp = deepcopy(distances)
		for i in range(len(self.offset)):
			if self.offset[i][literal[0]] != literal[1]:
				temp[i] -= 1
		return min(temp)

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
