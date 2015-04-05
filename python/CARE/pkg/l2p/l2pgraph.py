# @author: Lvl2Pillow

# Basic Graph class which can be exported as a DOT file.

################################################################################

from heapq import heappush, heappop
from random import choice

from l2pstat import rescale
from l2pcoll import intersect

################################################################################

class Node:
	# A node is uniquely identified by its node_id.
	def __init__(self, node_id, **properties):
		self.node_id = node_id
		self.properties = properties
		self.edges = []				# all edges connected to the node

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 

	def edit_properties(self, **properties):
		for key, value in list(properties.items()):
			self.properties[key] = value

	def set_property(self, key, value):
		self.properties[key] = value

	def remove_property(self, key):
		return self.properties(key, None)

	def add_edge(self, edge_id):
		if edge_id not in self.edges:
			self.edges.append(edge_id)

	def remove_edge(self, edge_id):
		if edge_id in self.edges:
			self.edges.remove(edge_id)

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 

	# Returns all unique neighbors.
	def neighbors(self):
		return list(set(self.in_neighbors()+self.out_neighbors()))

	# Returns all neighbor nodes which have an edge going into this node.
	# NOTE: This function assumes that the edge_id = (head_id, tail_id).
	def in_neighbors(self):
		return [head_id for tail_id, head_id in self.edges \
			if head_id != self.node_id]

	# Returns all neighbor nodes, which have an edge coming from this node.
	# NOTE: This function assumes that the edge_id = (head_id, tail_id).
	def out_neighbors(self):
		return [tail_id for tail_id, head_id in self.edges \
			if tail_id != self.node_id]

################################################################################

class Edge:
	# An edge is uniquely identified by its head_id & tail_id. The ordering
	# is important.
	def __init__(self, head_id, tail_id, **properties):
		self.head_id = head_id			# head node
		self.tail_id = tail_id			# tail node
		self.properties = properties

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 

	def edit_properties(self, **properties):
		for key, value in list(properties.items()):
			self.properties[key] = value

	def set_property(self, key, value):
		self.properties[key] = value

	def remove_property(self, key):
		return self.properties(key, None)

################################################################################

# Basic undirected graph. Does not support multigraph/ multiple edges between
# 2 nodes.
class Graph:
	def __init__(self, **properties):
		self.nodes = {}
		self.edges = {}
		self.properties = properties

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 

	def edit_properties(self, **properties):
		for key, value in list(properties.items()):
			self.properties[key] = value

	def set_property(self, key, value):
		self.properties[key] = value

	def remove_property(self, key):
		return self.properties(key, None)

	# Removes all nodes and all edges.
	def clear(self):
		self.nodes = {}
		self.edges = {}

	# Adds a node to the graph. If the node already exists, it will be replaced.
	def add_node(self, node_id, **properties):
		# initialize and add node
		node = Node(node_id, **properties)
		self.nodes[node_id] = node
		return node

	# Removes a node from the graph, and also all connected edges.
	def remove_node(self, node_id):
		if node_id in self.nodes.keys():
			# remove all connected edges
			for edge_id in self.nodes[node_id].edges:
				self.remove_edge(edge_id)
			# remove the node
			return self.nodes.pop(node_id)
		return None

	# Adds an edge between 2 nodes. If the edge already exists, it will be
	# replaced. Head and tail nodes are created implicitly if they do not exist.
	def add_edge(self, head_id, tail_id, **properties):
		# implicitly creates head and tail nodes
		if head_id not in self.nodes.keys():
			self.add_node(head_id)
		if tail_id not in self.nodes.keys():
			self.add_node(tail_id)
		# generate an edge_id
		edge_id = self.generate_edge_id(head_id, tail_id)
		# initialize and add edge
		edge = Edge(head_id, tail_id, **properties)
		self.edges[edge_id] = edge
		# update head and tail nodes
		# NOTE: Special case when head = tail, but is handled in the Node class.
		self.nodes[head_id].add_edge(edge_id)
		self.nodes[tail_id].add_edge(edge_id)
		return edge

	# Removes an edge between 2 nodes from the graph. This function can either
	# take a single argument, which is the edge_id, or 2 arguments, which are
	# the head_id and tail_id.
	def remove_edge(self, *args):
		# edge_id
		if len(args) == 1:
			edge_id = args[0]
			# remove edges from nodes
			self.nodes[edge_id[0]].remove_edge(edge_id)	# remove from head node
			self.nodes[edge_id[1]].remove_edge(edge_id)	# remove from tail node
			# remove the edge
			return self.edges.pop(edge_id)				# remove edge
		# head_id and tail_id
		elif len(args) == 2:
			edge_id = self.generate_edge_id(args[0], args[1])
			self.remove_edge(edge_id)
		else:
			raise TypeError

	# Generates a unique edge_id given the head and tail nodes of the edge.
	# For undirected graph, this is an ordered tuple containing the head_id,
	# and tail_id.
	def generate_edge_id(self, node_id, neighbor_id):
		return (node_id, neighbor_id) \
			if node_id <= neighbor_id else (neighbor_id, node_id)

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 

	# Generates a graph from an adjacency matrix, i.e. a 2D list.
	# NOTE: This function only supports undirected graphs.
	# NOTE: This function does not support custom node names.
	def from_adjacency_matrix(self, matrix, invert = False):
		# clear the graph first
		self.clear()
		# inverted adjacency matrix for betweenness centrality
		if invert:
			matrix = self.invert(matrix)
		# add the nodes
		num_nodes = len(matrix)
		for i in range(num_nodes):
			self.add_node(i)
		# add the edges
		for i in range(num_nodes):
			# NOTE: Only need to traverse half of the matrix for undirected graph.
			for j in range(i+1, num_nodes):
				if matrix[i][j]:
					self.add_edge(i, j, adj_value = matrix[i][j])
		return self

	# Returns an adjacency matrix where all edge values are inversed.
	# This function is useful for transforming between weight/ distance.
	def invert(self, matrix):
		num_vars = len(matrix)
		max_ = max([max([x for x in row]) for row in matrix])
		inversed_matrix = [[0 for i in range(num_vars)] for j in range(num_vars)]
		for i in range(num_vars):
			for j in range(num_vars):
				if matrix[i][j]:
					# offset by 1
					inversed_matrix[i][j] = 1+max_-matrix[i][j]
		return inversed_matrix

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 

	# Returns whether the graph is connected of not.
	def is_connected(self):
		if not self.nodes:
			return false
		# Search for all nodes using DFS
		# random starting node
		root = self.nodes[choice(list(self.nodes.keys()))]
		S = [root]			# stack
		visited = set()				# set
		visited.add(root)
		while S:
			v = S.pop()
			for u in v.neighbors():
				if u not in visited:
					S.add(u)
					visited.add(u)
		return True if len(self.nodes) == len(visited) else False

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 

	# Returns a dictionary of degree centralities of each node. A weight for 
	# each edge can be specified.
	def degree_centrality(self, weight = 'adj_value', normalize = True):
		degree = {node_id: sum([self.edges[edge_id].properties[weight] \
			if weight in self.edges[edge_id].properties.keys() else 1.0 \
			for edge_id in node.edges]) \
			for node_id, node in list(self.nodes.items())}
		if normalize:
			return rescale(degree)
		else:
			return degree

	# Returns a dictionary of betweenness centralities of each node. The 
	# betweenness centrality of a node 'v' is the sum of the fraction of all 
	# pairs shortest paths that pass through 'v'. A distance for each edge can 
	# be specified.
	# Based of Brande's algorithm for weighted betweenness centrality.
	# http://www.inf.uni-konstanz.de/algo/publications/b-fabc-01.pdf
	def betweenness_centrality(self, distance = 'adj_value', normalize = True):
		betweenness = dict.fromkeys(self.nodes.keys(), 0.0)
		for node_id in betweenness.keys():
			# using Dijkstra's algorithm
			self.dijkstra_path(betweenness, node_id, distance = distance)
		if normalize:
			return rescale(betweenness)
		else:
			return betweenness

	# Returns a dictionary of degree centralities of each node, to a subset
	# group of nodes. This means the number of connections a particular node
	# shares with a specified group of nodes. A weight for each edge can be
	# specified.
	# @param subset: node_ids
	def sub_degree_centrality(self, weight = 'adj_value', normalize = True, *subset):
		sub = { node_id: sum([self.edges[edge_id].properties[weight] \
			if weight in self.edges[edge_id].properties.keys() else 1.0 \
			for edge_id in node.edges if intersect(list(edge_id), list(subset))]) \
			for node_id, node in list(self.nodes.items()) if node_id not in list(subset)}
		# fill missing values with 0
		for node_id in range(len(self.nodes)):
			if node_id not in sub.keys():
				sub[node_id] = 0
		if normalize:
			return rescale(sub)
		else:
			return sub

	def dijkstra_path(self, betweenness, node_id, distance = ''):
		S = []
		P = {v: [] for v in betweenness.keys()}
		sigma = dict.fromkeys(betweenness.keys(), 0.0)
		sigma[node_id] = 1.0
		D = {}
		seen = {node_id: 0}
		Q = []
		heappush(Q, (0, node_id, node_id))
		while Q:
			(distance, predecessor, v) = heappop(Q)
			# already searched this node
			if v in D.keys():
				continue
			# count paths
			sigma[v] += sigma[predecessor]
			S.append(v)
			D[v] = distance
			for w in self.nodes[v].neighbors():
				edge_id = self.generate_edge_id(v, w)
				vw_distance = distance+\
					(self.edges[edge_id].properties[distance] \
					if distance in self.edges[edge_id].properties.keys() \
					else 1.0)
				if (w not in D.keys() and \
					(w not in seen.keys() or vw_distance < seen[w])):
					seen[w] = vw_distance
					heappush(Q, (vw_distance, v, w))
					sigma[w] = 0.0
					P[w] = [v]
				elif vw_distance == seen[w]:
					sigma[w] += sigma[v]
					P[w].append(v)
		# accumulation
		delta = dict.fromkeys(S, 0)
		while S:
			u = S.pop()
			coefficient = (1.0+delta[u])/sigma[u]
			for v in P[u]:
				delta[v] += sigma[v]*coefficient
			if u != node_id:
				betweenness[u] += delta[u]

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 

	# Basic function to export graph to DOT file for Graphviz.
	# NOTE: This function assumes that the edge_id = (head_id, tail_id).
	def write_dot(self, filename):
		try:
			with open(filename, 'w') as output:
				# graph name or default
				if 'name' not in self.properties.keys():
					output.write('// default name\ngraph G {\n')
				else:
					output.write('graph %s {\n' %self.properties['name'])
				# nodes
				for node_id, node in list(self.nodes.items()):
					# node properties
					properties = ', '.join([str(key)+'=\"'+str(value)+'\"' \
						for key, value in list(node.properties.items())])
					output.write('\t%s [%s];\n' %(node_id, properties))
				# edges
				for edge_id, edge in list(self.edges.items()):
					# edge properties
					properties = ', '.join([str(key)+'=\"'+str(value)+'\"' \
						for key, value in list(edge.properties.items())])
					output.write('\t%s -- %s [%s];\n' \
						%(edge_id[0], edge_id[1], properties))
				output.write('}')
		except IOError:
			print('Failed to write DOT file.')
		finally:
			output.close()

################################################################################
