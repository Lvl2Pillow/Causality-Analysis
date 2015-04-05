# Causality Reasoning (CARE)
# @author: Henry Su 2014

# To run this Python application, you will first need to have Python installed
# on your system.
# Open any command line and navigate to the root folder "pkg". Then enter:
#	- for Python 2.7+: 	python main.py
#	- for Python 3+: 	python3 main.py 

################################################################################

try:
	# Python 2.7+
	from Tkinter import *
	from tkFileDialog import *
except ImportError:
	# Python 3+
	from tkinter import *
	from tkinter.filedialog import *

from idlelib.WidgetRedirector import WidgetRedirector
from platform import system
from re import search
from time import sleep

from pkg.logger import *
from pkg.care.data import *
from pkg.care.search import *
from pkg.care.expansion import *
from pkg.care.cover import *

################################################################################

# Read-only Text widget for Tkinter.
# Allows text formatting on matches to regular expression patterns.
class ReadOnlyText(Text):
	def __init__(self, *args, **kwargs):
		Text.__init__(self, *args, **kwargs)
		self.redirector = WidgetRedirector(self)
		# change insert and delete bindings
		self.insert = self.redirector.register('insert', lambda *args, **kw: 'break')
		self.delete = self.redirector.register('delete', lambda *args, **kw: 'break')

	# Formats all matches to a regular expression pattern with a given tag.
	def format_pattern(self, pattern, tag, start = '1.0', end = 'end'):
		start = self.index(start)
		end = self.index(end)
		self.mark_set('matchStart', start)
		self.mark_set('matchEnd', start)
		self.mark_set('searchLimit', end)

		count = IntVar()
		while True:
			index = self.search(pattern, 'matchEnd', 'searchLimit', count = count, regexp = True)
			if not index:
				break
			self.mark_set('matchStart', index)
			self.mark_set('matchEnd', '%s+%sc' % (index, count.get()))
			self.tag_add(tag, 'matchStart', 'matchEnd')

################################################################################

# Tkinter application.
# NOTE: Tkinter is part of the Python standard lib. No external modules are required.
# NOTE: This application should on Windows, Mac, and Linux platforms.
class CARE(Tk):
	def __init__(self, master = None):
		Tk.__init__(self, master)
		# constant variables (default Mac)
		self.lf_width = 360						# width of the left frame
		# NOTE: currently using default background colors
		self.color_0 = '#FFFFFF'				# background color
		self.color_1 = '#000000'				# default text color
		self.color_2 = '#D3D3D3'				# disabled color
		self.color_3 = '#808080'				# separator color
		self.button_padx = 4					# padding left and right of buttons
		self.button_pady = 4					# padding above and below buttons
		self.separator_pady = 8					# padding above and below separators
		self.separator_width = 2				# width of separators
		self.default_font = 'sans-serif 12'		# default font
		self.bold_font = 'sans-serif 12 bold'	# bold font
		self.small_font = 'sans-serif 10'		# small font
		# adjust variables for Windows
		if system() == 'Windows':
			self.lf_width = 360
			self.button_padx = 8
			self.button_pady = 16
		# add widgets
		self.add_widgets()
		# initial message
		self.log('> Choose the file containing the input dataset.\n'+\
			'Note: The file must be *.csv format.')

	# Add widgets to the application window.
	# The application is split into 2 main frames - left & right.
	# The left frame contains user control widgets.
	# The right frame displays a log.
	# * * * * * * * * * * * * * * * *
	# *			*					*
	# *			*					*
	# *	User	*	Log 			*
	# *	Control	*	Messages		*
	# *			*					*
	# *			*					*
	# * * * * * * * * * * * * * * * *
	def add_widgets(self):
		# application title
		self.title('Causality Reasoning (CARE)')
		# disable window resizing
		self.resizable(height = False, width = False)

		# basic structure and layout of the application
		left_frame = Frame(self)
		left_frame.grid(column = 0, row = 0, sticky = 'wens')
		separator = Frame(self, background = self.color_3, width = self.separator_width)
		separator.grid(column = 1, row = 0, sticky = 'ns')
		right_frame = Frame(self)
		right_frame.grid(column = 2, row = 0)

		# add widgets to the right frame
		ro_text = ReadOnlyText(right_frame, font = self.default_font, wrap = 'none')
		ro_text.grid(column = 0, row = 0)
		# add horizontal and vertical scrollbars
		text_hscroll = Scrollbar(right_frame, command = ro_text.xview, orient = 'horizontal', width = 0)
		text_hscroll.grid(column = 0, row = 1, sticky = 'we')
		text_vscroll = Scrollbar(right_frame, command = ro_text.yview, width = 0)
		text_vscroll.grid(column = 1, row = 0, sticky = 'ns')
		ro_text.configure(xscrollcommand = text_hscroll.set, yscrollcommand = text_vscroll.set)

		# Create a Logger object that is linked to the Text widget.
		# The Logger object is used by other classes to communicate messages.
		self.logger = Logger(self, ro_text)

		# create a canvas for the left frame
		# the canvas contains various other widgets
		self.canvas = Canvas(left_frame, width = self.lf_width)
		self.canvas.pack(expand = True, fill = 'both', side = 'left')
		canvas_vscroll = Scrollbar(left_frame, command = self.canvas.yview)
		canvas_vscroll.pack(fill = 'y', side = 'right')
		inner_frame = Frame(self.canvas)
		inner_frame.pack(expand = True, fill = 'both')
		self.canvas.configure(yscrollcommand = canvas_vscroll.set)
		self.canvas.create_window((0, 0), anchor = 'n', width = self.lf_width, window = inner_frame)
		inner_frame.bind('<Configure>', self.on_frame_configure)

		# add various widgets to the inner frame
		# padding
		top_padding = Frame(inner_frame, height = self.separator_pady/2)
		top_padding.pack(fill = 'x')
		# add title
		title_label = Label(inner_frame, font = 'Georgia 16 bold', text = 'Causality Reasoning')
		title_label.pack(fill = 'x', pady = 4)

		# horizontal separator
		separator_0 = Frame(inner_frame, background = self.color_3, height = self.separator_width)
		separator_0.pack(fill = 'x', pady = self.separator_pady)

		# widgets to browse input file
		self.browse_label = Label(inner_frame, anchor = 'w', font = self.bold_font, justify = 'left', 
			text = '1. Choose the input file.', wraplength = self.lf_width)
		self.browse_label.pack(fill = 'x')
		# header checkbox
		self.header_value = IntVar()
		self.header_value.set(1)
		self.header_check = Checkbutton(inner_frame, font = self.small_font, 
			text = "Header", variable = self.header_value)
		self.header_check.pack()
		self.browse_button = Button(inner_frame, command = self.browse, font = self.default_font, text = 'Browse')
		self.browse_button.pack(padx = self.button_padx, pady = self.button_pady)

		separator_1 = Frame(inner_frame, background = self.color_3, height = self.separator_width)
		separator_1.pack(fill = 'x', pady = self.separator_pady)

		# widgets to calibrate fuzzy dataset
		self.calibrate_label = Label(inner_frame, anchor = 'w', font = self.bold_font, justify = 'left', 
			text = '2. Calibrate fuzzy dataset.', wraplength = self.lf_width)
		self.calibrate_label.pack(fill = 'x')
		# radio buttons
		stat_container = Frame(inner_frame)
		stat_container.pack(fill = 'x')
		stat_inner_container = Frame(stat_container)
		stat_inner_container.pack()
		self.calibrate_value = IntVar()
		self.calibrate_value.set(2)
		self.radio_1 = Radiobutton(stat_inner_container, font = self.small_font, text = 'Mean', 
			value = 1, variable = self.calibrate_value)
		self.radio_1.pack(side = 'left')
		self.radio_2 = Radiobutton(stat_inner_container, font = self.small_font, text = 'Median', 
			value = 2, variable = self.calibrate_value)
		self.radio_2.pack(side = 'left')
		self.calibrate_button = Button(inner_frame, command = self.calibrate, font = self.default_font, text = 'Calibrate')
		self.calibrate_button.pack(padx = self.button_padx, pady = self.button_pady)
		# default disabled
		self.toggle_calibrate(state = False)
		
		separator_2 = Frame(inner_frame, background = self.color_3, height = self.separator_width)
		separator_2.pack(fill = 'x', pady = self.separator_pady)

		# widgets to run the causality analysis
		self.analyse_label = Label(inner_frame, anchor = 'w', font = self.bold_font, justify = 'left', 
			text = '3. Run the causality analysis.', wraplength = self.lf_width)
		self.analyse_label.pack(fill = 'x')
		# set heuristic influences
		heuristic_container0 = Frame(inner_frame)
		heuristic_container0.pack(fill = 'x', padx = 4)
		self.lf_label = Label(heuristic_container0, font = self.small_font, 
			anchor = 'n', height = 3, padx = 4, text = '\nLiteral Frequency')
		self.lf_label.pack(side = 'left')
		self.lf_val = StringVar()
		lf_entry = Entry(heuristic_container0, textvariable = self.lf_val)
		self.lf_val.set('0.5')
		lf_entry.pack(side = 'right')
		heuristic_container1 = Frame(inner_frame)
		heuristic_container1.pack(fill = 'x', padx = 4)
		self.degree_label = Label(heuristic_container1, font = self.small_font, 
			anchor = 'n', height = 3, padx = 4, text = '\nDegree Centrality')
		self.degree_label.pack(side = 'left')
		self.degree_val = StringVar()
		degree_entry = Entry(heuristic_container1, textvariable = self.degree_val)
		self.degree_val.set('0.25')
		degree_entry.pack(side = 'right')
		heuristic_container2 = Frame(inner_frame)
		heuristic_container2.pack(fill = 'x', padx = 4)
		self.between_label = Label(heuristic_container2, font = self.small_font, 
			anchor = 'n', height = 3, padx = 4, text = '\nBetweenness Centrality')
		self.between_label.pack(side = 'left')
		self.between_val = StringVar()
		between_entry = Entry(heuristic_container2, textvariable = self.between_val)
		self.between_val.set('0.25')
		between_entry.pack(side = 'right')
		heuristic_container3 = Frame(inner_frame)
		heuristic_container3.pack(fill = 'x', padx = 4)
		self.sub_label = Label(heuristic_container3, font = self.small_font, 
			anchor = 'n', height = 3, padx = 4, text = '\nSubset Degree Centrality')
		self.sub_label.pack(side = 'left')
		self.sub_val = StringVar()
		sub_entry = Entry(heuristic_container3, textvariable = self.sub_val)
		self.sub_val.set('0')
		sub_entry.pack(side = 'right')
		# similarity/ distinction scale
		dist_container = Frame(inner_frame)
		dist_container.pack(fill = 'x', padx = 4)
		self.dist_label = Label(dist_container, font = self.small_font, 
			anchor = 'n', height = 3, padx = 4, text = '\nRound to Resolution')
		self.dist_label.pack(side = 'left')
		self.dist_val = StringVar()
		dist_entry = Entry(dist_container, textvariable = self.dist_val)
		self.dist_val.set('0.00001')
		dist_entry.pack(side = 'right')
		self.analyse_button = Button(inner_frame, command = self.analyse, font = self.default_font, text = 'Analyse')
		self.analyse_button.pack(padx = self.button_padx, pady = self.button_pady)
		self.toggle_analyse(state = False)

# 		separator_3 = Frame(inner_frame, background = self.color_3, height = self.separator_width)
# 		separator_3.pack(fill = 'x', pady = self.separator_pady)

# 		# widgets to generate network graphs
# 		self.graph_label = Label(inner_frame, anchor = 'w', font = self.bold_font, justify = 'left', 
# 			text = '4. Generate network graphs.', wraplength = self.lf_width)
# 		self.graph_label.pack(fill = 'x')
# 		self.graph_button = Button(inner_frame, command = self.graph, font = self.default_font, text = 'Graph')
# 		self.graph_button.pack(padx = self.button_padx, pady = self.button_pady)
# 		self.toggle_graph(state = False)

		# extra padding
		bottom_padding = Frame(inner_frame, height = self.separator_pady)
		bottom_padding.pack(fill = 'x')

	def on_frame_configure(self, event):
		# reset the scroll region to encompass the inner frame
		self.canvas.configure(scrollregion = self.canvas.bbox('all'))

	# Restrict user input to numeric characters only. Returns true if input is
	# positive numeric value.
	def validate_numeric(self, input):
		return (not search('[^0-9\.]', input))

	# Toggles the calibration widgets between 'disabled' and 'normal'.
	def toggle_calibrate(self, state = False):
		if (state):
			self.log('> Fuzzy dataset must be calibrated before the causality analysis.')
			self.log('Note: You may choose a statistical method for calculating the threshold.')
			self.calibrate_label.configure(foreground = self.color_1)
			self.radio_1.configure(state = 'normal')
			self.radio_2.configure(state = 'normal')
			self.calibrate_button.configure(state = 'normal')
		else:
			self.calibrate_label.configure(foreground = self.color_2)
			self.radio_1.configure(state = 'disabled')
			self.radio_2.configure(state = 'disabled')
			self.calibrate_button.configure(state = 'disabled')

	# Toggles the analysis widgets between 'disabled' and 'normal'.
	def toggle_analyse(self, state = False):
		if (state):
			self.log('> Ready to run the causality analysis.')
			self.log('Note: You may adjust the first scale to set the amount'+\
				' of interactions between causal variables.')
			self.log('Note: You may adjust the second scale to set the chances of a tie between literals.')

		# 	self.analyse_label.configure(foreground = self.color_1)
		# 	self.social_label_1.configure(foreground = self.color_1)
		# 	self.social_label_2.configure(foreground = self.color_1)
		# 	self.random_label_1.configure(foreground = self.color_1)
		# 	self.random_label_2.configure(foreground = self.color_1)
		# 	self.social_scale.configure(foreground = self.color_1, state = 'normal')
		# 	self.random_scale.configure(foreground = self.color_1, state = 'normal')
		# 	self.analyse_button.configure(state = 'normal')
		# else:
		# 	self.analyse_label.configure(foreground = self.color_2)
		# 	self.social_label_1.configure(foreground = self.color_2)
		# 	self.social_label_2.configure(foreground = self.color_2)
		# 	self.random_label_1.configure(foreground = self.color_2)
		# 	self.random_label_2.configure(foreground = self.color_2)
		# 	self.social_scale.configure(foreground = self.color_2, state = 'disabled')
		# 	self.random_scale.configure(foreground = self.color_2, state = 'disabled')
		# 	self.analyse_button.configure(state = 'disabled')

# 	def toggle_graph(self, state = False):
# 		if (state):
# 			self.graph_label.configure(foreground = self.color_1)
# 			self.graph_button.configure(state = 'normal')
# 		else:
# 			self.graph_label.configure(foreground = self.color_2)
# 			self.graph_button.configure(state = 'disabled')

	# Browse files for input dataset.
	def browse(self):
		pass
		# only CSV format
		filename = askopenfilename(filetypes = [('CSV', '*.csv')])
		if (filename):
			try:
				self.log('Reading:\n'+filename)
				# new Data object
				self.D = Data(filename, header = (self.header_value.get() == 1), logger = self.logger)
				# validation check
				if not self.D.is_valid():
					self.log('Error! Input data is invalid!')
					return
				# read
				self.variables, self.onset, self.offset = self.D.read()
				self.log('Variables: '+', '.join(self.variables))
				self.log('Snippet of onset:', self.onset, line_limit = 32)
				self.log('Snippet of offset:', self.offset, line_limit = 32)
				self.log('Number of variables: '+str(len(self.variables))+'\n'+\
					'Number of entries from onset: '+str(len(self.onset))+'\n'+\
					'Number of entries from offset: '+str(len(self.offset)))
				# Fuzzy/ crisp dataset
				if not self.D.is_crisp:
					self.log('Warning! Dataset is not crisp. Calibration required.')
					self.toggle_calibrate(state = True)
					# self.toggle_analyse(state = False)
					# self.toggle_graph(state = False)
				else:
					self.toggle_calibrate(state = False)
					# self.toggle_analyse(state = True)
					# self.toggle_graph(state = False)
					# clean dataset
					self.D.clean()
			except (IOError):
				self.log('Error! Failed to open file: '+filename)
				return
			except Exception:
				self.log('Error! Oops, something went horribly wrong...')
				return

	# Calibrates a fuzzy dataset to a rough crisp dataset.
	# NOTE: This function should never be called if the dataset is already crisp.
	def calibrate(self):
		# convert the input dataset using a statistical method
		if (self.calibrate_value.get() == 1):
			self.log('Calibrating fuzzy to rough crisp using the mean as a threshold.')
			self.onset, self.offset = self.D.calibrate(stat = 'mean')
		elif (self.calibrate_value.get() == 2):
			self.log('Calibrating fuzzy to rough crisp using the median as a threshold.')
			self.onset, self.offset = self.D.calibrate(stat = 'median')
		# check if the calibration was successful and update the GUI
		if self.D.is_crisp:
			self.log('Calibration completed.')
			self.log('Snippet of the onset:', self.onset, line_limit = 4)
			self.log('Snippet of the offset:', self.offset, line_limit = 4)
			self.toggle_calibrate(state = False)
			# self.toggle_analyse(state = True)
			# self.toggle_graph(state = False)
		else:
			self.log('Warning! Failed to calibrate fuzzy to rough crisp.')
			self.toggle_calibrate(state = True)
			# self.toggle_analyse(state = False)
			# self.toggle_graph(state = False)

	# Runs the core causality analysis.
	# NOTE: This may take a while depending on the size of the dataset and the
	# amount of similarity between literals.
	def analyse(self):
		# input validation
		if not all([self.validate_numeric(self.lf_val.get()), 
				self.validate_numeric(self.degree_val.get()), 
				self.validate_numeric(self.between_val.get()), 
				self.validate_numeric(self.sub_val.get())]):
			self.log('Error! Heuristic values must be positive numeric values.')
			return
		if float(self.lf_val.get()) == 0 and float(self.degree_val.get()) == 0 and \
			float(self.between_val.get()) == 0 and float(self.sub_val.get()) == 0:
			self.log('Error! At least one heuristic must be > 0.')
			return
		# warnings
		if float(self.dist_val.get()) > 0.00001:
			self.log('Warning! Rough rounding may cause multiple ties and result '+\
				'in a slower analysis.')
		self.log('Beginning the causality analysis.')
		# run modified CARE algorithm
		CDS = CDSearch(self.onset, self.offset, 
			[float(self.lf_val.get()), float(self.degree_val.get()), 
			float(self.between_val.get()), float(self.sub_val.get())], 
			resolution = float(self.dist_val.get()), logger = self.logger)
		Is = CDS.cd_search()
		self.log(matrix = self.replace_name(Is, self.variables), line_limit = 512)
		self.log(str(len(Is))+' implicants were found.')
		IE = ImplicantExpansion(Is, self.offset, CDS.degree, CDS.betweenness, logger = self.logger)
		PIs = IE.implicant_expansion()
		self.log(matrix = self.replace_name(PIs, self.variables))
		self.log(str(len(PIs))+' prime implicants were found.')
		UC = UnateCover(PIs, self.onset, logger = self.logger)
		EPIs = UC.unate_cover()
		self.log(matrix = self.replace_name(EPIs, self.variables))
		self.log(str(len(EPIs))+' essential prime implicants were found.')
		self.log('Finished the causality analysis.')

# 	# Draws various network graphs to summarise the entire causality analysis.
# 	# TODO: Create a module which generates a network graph skeleton, which can
# 	# be converted into a visual graph using any online graphing tool.
# 	def graph(self):
# 		pass

	def replace_name(self, matrix, variables):
		new_matrix = []
		for row in range(len(matrix)):
			new_matrix.append([])
			for literal in matrix[row]:
				if literal[1]:
					new_matrix[row].append(variables[literal[0]])
				else:
					new_matrix[row].append('~'+variables[literal[0]])
		return new_matrix

# ################################################################################

	# Helper function for logging messages.
	def log(self, message = '', matrix = '', line_wrap = 240, line_limit = 256):
		if self.logger:
			self.logger.log(message, matrix, line_wrap, line_limit)

################################################################################

# Start application.
if __name__ == '__main__':
	CARE().mainloop()

################################################################################
