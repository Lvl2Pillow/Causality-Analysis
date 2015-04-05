# Causality Reasoning
# @author: Henry Su 2014

################################################################################

try:
	# Python 2.7+
	from tkFont import *
except ImportError:
	# Python 3+
	from tkinter.font import *

from datetime import datetime

################################################################################

# Logging object used to log messages from other classes. After initializing the
# Logger object, it can be passed as an argument to other classes, and used to
# log messages to a central logging object.
class Logger:
	# @param Text: a Tkinter Text object
	def __init__(self, master, Text):
		self.master = master		# Tkinter master
		self.Text = Text			# Tkinter Text object
		# text formats
		default_font = 'sans-serif 12'
		bold_font = 'sans-serif 12 bold'
		italic_font = 'sans-serif 12 italic'
		# custom tags to format the text
		self.Text.tag_configure('timestamp', font = italic_font, underline = True)
		self.Text.tag_configure('instruction', font = bold_font)
		self.Text.tag_configure('note', font = italic_font, foreground = 'grey')
		self.Text.tag_configure('warning', font = bold_font, foreground = 'orange')
		self.Text.tag_configure('error', font = bold_font, foreground = 'red')

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 

	# Logs a message to the Text object.
	def log(self, message = '', matrix = [], line_wrap = 240, line_limit = 256):
		# timestamp
		self.Text.insert('end', self.current_time()+':\n')
		# split the message into lines
		message_lines = message.split('\n')
		# convert each row of the matrix into a line
		matrix_lines = [','.join(str(x) for x in matrix[i]) for i in range(min(len(matrix), line_limit))]
		# log the message and matrix
		self.log_helper(message_lines, line_wrap, line_limit)
		self.log_helper(matrix_lines, line_wrap, line_limit)
		# apply custom formatting to regexp matches
		self.Text.format_pattern('^[0-9]{2}:[0-9]{2}:[0-9]{2}\.[0-9]{3}:\\n', 'timestamp')
		self.Text.format_pattern('^>.*?\\n', 'instruction')
		self.Text.format_pattern('^Note.*?\\n', 'note')
		self.Text.format_pattern('^Warning.*?\\n', 'warning')
		self.Text.format_pattern('^Error.*?\\n', 'error')
		# change scroll view to bottom of the Text box
		self.Text.see('end')
		# update the change immediately
		self.master.update_idletasks()

	# Writes a message and/ or matrix to the log. The message/ matrix can span
	# across multiple lines.
	def log_helper(self, lines, line_wrap, line_limit):
		# only loop the minimum number of times
		for i in range(min(len(lines), line_limit)):
			# check if each line exceeds the character limit
			ellipses = '...'
			if (len(lines[i]) < line_wrap-len(ellipses)):
					self.Text.insert('end', lines[i]+'\n')
			else:
				# truncate the line and add ellipses to the end
				self.Text.insert('end', lines[i][:line_wrap-len(ellipses)]+ellipses+'\n')
		# if the number of lines in the message exceeds the maximum limit
		if len(lines) > line_limit:
			self.Text.insert('end', ellipses+'\n')

	# Returns the current time.
	def current_time(self):
		return datetime.now().strftime('%H:%M:%S.%f')[:12]

################################################################################
