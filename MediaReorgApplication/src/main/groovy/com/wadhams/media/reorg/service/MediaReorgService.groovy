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
import com.wadhams.media.reorg.type.RenameMethod

class MediaReorgService {
	SimpleDateFormat sdf1 = new SimpleDateFormat('yyyyMMdd_HHmmss')
	SimpleDateFormat sdf2 = new SimpleDateFormat('EEE MMM dd hh:mm:ss yyyy')	//Sat Jun  8 17:33:06 2013
	NumberFormat nf6 = NumberFormat.getNumberInstance() 
	
	def MediaReorgService() {
		nf6 = NumberFormat.getNumberInstance()
		nf6.setGroupingUsed(false)
		nf6.setMinimumIntegerDigits(6)

	}
	
	List<AppMedia> findAllFiles(String folderPath) {
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
			println "ImageProcessingException on ${f.name}: ${ipe.message}"
			println ''
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

	Date findLastModified(File f) {
		long lm = f.lastModified()
		
		Date lastModified = new Date(lm)
		
		return lastModified
	}

	String buildNewFilename(AppMedia am, AppContext context) {
		StringBuilder sb = new StringBuilder()
		
		String prefix
		if (context.renameMethod == RenameMethod.CreationDate) {
			if (am.creationDate) {
				prefix = sdf1.format(am.creationDate)
			}
			else {
				return null		//renameMethod.CreationDate requires a creation date, otherwise the file is never renamed.
			}
		}
		else if (context.renameMethod == RenameMethod.LastModified) {
			if (am.lastModified) {
				prefix = sdf1.format(am.lastModified)
			}
			else {
				return null		//renameMethod.LastModified requires a creation date, otherwise the file is never renamed.
			}
		}
		else {	//renameMethod.Timestamp
			prefix = context.groupDate
		}
		
		sb.append(prefix)
		sb.append('_')
		sb.append(context.groupName)
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
			return null		//catastrophic problem with file extension
		}
		
		return sb.toString()
	}
	
	def report(AppContext context) {
		int jpgCount = 0
		int movCount = 0
		int mp4Count = 0
		int aviCount = 0
		int heicCount = 0
		int unknownMediaCount = 0
		int noCreationDateCount = 0
		int noLastModifiedCount = 0
		
		context.appMediaList.each {am ->
			println "Filename...: ${am.file.name}"

			if (context.renameMethod == RenameMethod.CreationDate && am.creationDate == null) {
				println "\t*** No creation date"
				noCreationDateCount++
			}
			else if (context.renameMethod == RenameMethod.LastModified && am.lastModified == null) {
				println "\t*** No last modified"
				noLastModifiedCount++
			}
			
			//Media counts
			if (am.media == Media.JPG) {
				jpgCount++
			}
			else if (am.media == Media.MOV) {
				movCount++
			}
			else if (am.media == Media.MP4) {
				mp4Count++
			}
			else if (am.media == Media.AVI) {
				aviCount++
			}
			else if (am.media == Media.HEIC) {
				heicCount++
			}
			else {
				println "\t*** *** Unknown media"
				unknownMediaCount++
			}
			
			println "\tProposed new filename: ${am.newFilename}"
		}
		println ''
		println 'File Count Totals'
		println '-----------------'
		println "jpg files..........: $jpgCount"
		println "mov files..........: $movCount"
		println "mp4 files..........: $mp4Count"
		println "avi files..........: $aviCount"
		println "heic files.........: $heicCount"
		println "unknown media......: $unknownMediaCount"
		println ''
		if (context.renameMethod == RenameMethod.CreationDate) {
			println "No creation date...: $noCreationDateCount"
			println ''
		}
		else if (context.renameMethod == RenameMethod.LastModified) {
			println "No last modified...: $noLastModifiedCount"
			println ''
		}
	}
	
	def renameFile(File f, String newFilename) {
		String rename = "${f.parent}\\$newFilename"
		println "Renaming...${f.path} to: $rename"
		f.renameTo(rename)
	}

}
