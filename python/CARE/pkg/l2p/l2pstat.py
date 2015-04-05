# @author: Lvl2Pillow

# Collection of functions for handling statistics.

from math import factorial

################################################################################

# Returns the mean value of a list.
def mean(list_):
	return sum(list_)/float(len(list_))

# Returns the median value of a list.
def median(list_):
	sorted_list = sorted(list_)
	if len(sorted_list)%2:
		return (sorted_list[int(len(sorted_list)/2)]+sorted_list[int(len(sorted_list)/2+1)])/2.0
	else:
		return sorted_list[int(len(sorted_list)/2+1)]

# Feature normalization: rescale values to within the range [0, 1].
def rescale(iter_):
	# dict
	if type(iter_) is dict:
		values = iter_.values()
		min_ = min(values)
		max_ = max(values)
		# special case if max_ = min_
		if min_ == max_:
			return dict.fromkeys(iter_.keys(), 0.0)
		else:
			return {key: (iter_[key]-min_)/float(max_-min_) for key in iter_.keys()}
	# list, tuple, set
	else:
		min_ = min(iter_)
		max_ = max(iter_)
		if min_ == max_:
			return [0.0 for i in range(len(iter_))]
		else:
			return [(iter_[i]-min_)/float(max_-min_) for i in range(len(iter_))]

# Returns the compliment of a probability, i.e. 1-P.
def compliment(P):
	return 1.0-P

# The probability of at least one event succeeds.
def probability_at_least(*probabilities):
	probability_all_fail = 1.0
	for P in probabilities:
		probability_all_fail *= compliment(P)
	return compliment(probability_all_fail)

# Choose
def nCr(n, r):
	return factorial(n)/factorial(r)/factorial(n-r)