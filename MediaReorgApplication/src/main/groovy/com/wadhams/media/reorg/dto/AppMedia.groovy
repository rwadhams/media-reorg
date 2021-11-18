package com.wadhams.media.reorg.dto

import java.util.regex.Pattern

import com.wadhams.media.reorg.type.Media

import groovy.transform.ToString

@ToString(includeNames=true)
class AppMedia {
	static Pattern extensionPattern = ~/.*\.(\w{3,4})$/
	static int STARTING_SEQUENCE = 0
	
	File file
	Date creationDate
	Media media
	String extension
	int sequenceNumber
	String newFilename

	def AppMedia(File f) {
		this.file = f
		this.creationDate = null
		this.media = Media.findByFileExtension(f)
		if (this.media == Media.Unknown) {
			def m = f.name =~ extensionPattern
			if (m) {
				extension = m[0][1]
			}
		}
		this.sequenceNumber = ++STARTING_SEQUENCE
		this.newFilename = null
	}
}
