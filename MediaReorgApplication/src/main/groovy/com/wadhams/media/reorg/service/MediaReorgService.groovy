package com.wadhams.media.reorg.service

import static groovy.io.FileType.FILES

import java.text.NumberFormat
import java.text.SimpleDateFormat

import com.drew.imaging.ImageMetadataReader
import com.drew.imaging.ImageProcessingException
import com.drew.metadata.Metadata
import com.drew.metadata.avi.AviDirectory
import com.drew.metadata.exif.ExifSubIFDDirectory
import com.drew.metadata.mov.metadata.QuickTimeMetadataDirectory
import com.wadhams.media.reorg.context.AppContext
import com.wadhams.media.reorg.dto.AppMedia
import com.wadhams.media.reorg.type.Media

class MediaReorgService {
	SimpleDateFormat sdf1 = new SimpleDateFormat('yyyyMMdd_HHmmss')
	SimpleDateFormat sdf2 = new SimpleDateFormat('EEE MMM dd hh:mm:ss yyyy')	//Sat Jun  8 17:33:06 2013
	NumberFormat nf6 = NumberFormat.getNumberInstance() 
	
	def MediaReorgService() {
		nf6 = NumberFormat.getNumberInstance()
		nf6.setGroupingUsed(false)
		nf6.setMinimumIntegerDigits(6)

	}
	
	List<AppMedia> fileAllFiles(String folderPath) {
		List<AppMedia> appMediaList = []
		
		File dir = new File(folderPath)
		dir.eachFile(FILES) {f ->
			//println f.name
			appMediaList << new AppMedia(f)
		}

		return appMediaList
	}
	
	Date findCreationDate(File f, Media media) {
		Metadata metadata
		try {
			metadata = ImageMetadataReader.readMetadata(f)
		}
		catch(ImageProcessingException ipe) {
			println "${ipe.message}"
			return null
		}
		
//		for (Directory dir : metadata.getDirectories()) {
//			println dir.getClass()
//			for (Tag tag : dir.getTags()) {
//				println tag
//			}
//		}
		
		Date creationDate = null
		if (media == Media.JPG) {
			ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class)
			if (directory) {
				creationDate = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL, TimeZone.getDefault())
			}
		}
		else if (media == Media.MOV) {
			QuickTimeMetadataDirectory directory = metadata.getFirstDirectoryOfType(QuickTimeMetadataDirectory.class)
			creationDate = directory.getDate(QuickTimeMetadataDirectory.TAG_CREATION_DATE, TimeZone.getDefault())
		}
		else if (media == Media.HEIC) {
			ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class)
			creationDate = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL, TimeZone.getDefault())
		}
		else if (media == Media.AVI) {
			AviDirectory directory = metadata.getFirstDirectoryOfType(AviDirectory.class)
			String sDate = (String)directory.getObject(AviDirectory.TAG_DATETIME_ORIGINAL)
			creationDate = sdf2.parse(sDate)
		}
		else {
			//println "Unable to find creation date for: ${f.absolutePath}"
			//println ''
			return null
		}
		
		return creationDate
	}

	String buildNewFilename(AppMedia am, String groupName, String groupDate) {
		StringBuilder sb = new StringBuilder()
		
		if (am.creationDate) {
			sb.append(sdf1.format(am.creationDate))
		}
		else {
			sb.append(groupDate)
		}
		sb.append('_')
		sb.append(groupName)
		sb.append('_')
		sb.append(nf6.format(am.sequenceNumber))
		sb.append('.')
		if (am.media != Media.Unknown) {
			sb.append(am.media.extension)
		}
		else if (am.extension) {
			sb.append(am.extension)
		}
		else {
			return null		//catastrophic
		}
		
		return sb.toString()
	}
	
	def report(AppContext context) {
		int jpgCount = 0
		int movCount = 0
		int aviCount = 0
		int heicCount = 0
		int unknownCount = 0
		int noCreationDateCount = 0
		
		context.appMediaList.each {am ->
			println "Filename...: ${am.file.name}"
			if (!am.creationDate) {
				println "\t*** No creation date"
				noCreationDateCount++
			}
			if (am.media == Media.JPG) {
				jpgCount++
			}
			else if (am.media == Media.MOV) {
				movCount++
			}
			else if (am.media == Media.AVI) {
				aviCount++
			}
			else if (am.media == Media.HEIC) {
				heicCount++
			}
			else {
				println "\t*** *** Unknown media"
				unknownCount++
			}
			String newFilename = buildNewFilename(am, context.groupName, context.groupDate)
			if (newFilename) {
				println "\tProposed new filename: $newFilename"
			}
			else {
				println '*** UNABLE TO CREATE A NEW FILENAME. PLEASE INVESTIGATE ***'
			}
		}
		println ''
		println 'File Count Totals'
		println '-----------------'
		println "jpg files..........: $jpgCount"
		println "mov files..........: $movCount"
		println "avi files..........: $aviCount"
		println "heic files.........: $heicCount"
		println "unknown media......: $unknownCount"
		println ''
		println "No creation date...: $noCreationDateCount"
		println ''
	}
	
	def renameFile(File f, String newFilename) {
		String rename = "${f.parent}\\$newFilename"
		println "Renaming...${f.path} to: $rename"
		f.renameTo(rename)
	}

}
