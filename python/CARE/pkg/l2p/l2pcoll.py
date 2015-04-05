# @author: Lvl2Pillow

# Collection of functions for manipulating collections.

from math import sqrt

################################################################################

# Returns a dictionary of unique values and the index they appeared in the list.
# The length/ number of indexes is the count for the unique value.
def composition(list_):
	dict_ = {key: [] for key in set(list_)}
	for i in range(len(list_)):
		dict_[list_[i]].append(i)
	return dict_

# Return the intersection of two lists.
def intersect(list_x, list_y):
	return list(set(list_x) & set(list_y))

# Return the union of two lists.
def union(list_x, list_y):
	return list(set(list_x) | set(list_y))

# Deletes multiple elements from a list. This function will appear as if all
# elements were deleted simultaneously.
def multi_delete(list_, indexes):
	for i in sorted(indexes, reverse = True):
		del list_[i]

# Returns the Euclidean distance between 2 vectors.
def euclidean_distance(list_x, list_y):
	return sqrt(sum([(list_x[i]-list_y[i])**2 for i in range(len(list_x))]))

# Returns the Manhattan distance between 2 vectors.
def manhatten_distance(list_x, list_y):
	return sum([abs(list_x[i]-list_y[i]) for i in range(len(list_x))])

