package com.wadhams.media.reorg.type

import java.util.regex.Pattern

enum Media {
	JPG(['JPG','JPEG'], 'JPG'),
	MOV(['MOV'], 'MOV'),
	AVI(['AVI'], 'AVI'),
	HEIC(['HEIC'], 'HEIC'),
	Unknown(['Unknown'], '');
	
	private static Pattern extensionPattern = ~/.*\.(\w{3,4})$/
	
	private static EnumSet<Media> allEnums = EnumSet.allOf(Media.class)
	
	private final List<String> matchingExtensions
	private final String extension
	
	Media(List<String> matchingExtensions, String extension) {
		this.matchingExtensions = matchingExtensions
		this.extension = extension
	}
	
	public static Media findByFileExtension(File f) {
		String fileExtension = ''
		def m = f.name =~ extensionPattern
		if (m) {
			//println m[0]
			//println m[0][1]
			fileExtension = m[0][1]
		}
		
		if (fileExtension) {
			fileExtension = fileExtension.toUpperCase()
			for (Media e : allEnums) {
				if (e.matchingExtensions.contains(fileExtension)) {
					return e
				}
			}
		}
		else {
			println 'findByFileExtension() was passed a file without an extension'
			println ''
			return Media.Unknown
		}
		
		//println "Unknown extension: $fileExtension"
		//println ''
		return Media.Unknown
	}

}
