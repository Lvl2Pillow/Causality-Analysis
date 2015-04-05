# @author: Lvl2Pillow

# Collection of math related functions.

from math import floor

################################################################################

# Rounds a value to the nearest product of the resolution.
def round_resolution(value, resolution = 1.0):
	return round(value/resolution)*resolution

# Returns the arithmetic progression given the first, last, and delta values.
def arithmetic_progression(first, last, delta):
	return (floor((last-first)/delta)+1)*(first+last)/2.0
