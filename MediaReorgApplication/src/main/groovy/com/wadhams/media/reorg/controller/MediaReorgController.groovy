package com.wadhams.media.reorg.controller

import com.wadhams.media.reorg.context.AppContext
import com.wadhams.media.reorg.service.MediaReorgService
import com.wadhams.media.reorg.type.Action
import com.wadhams.media.reorg.type.RenameMethod

class MediaReorgController {
	AppContext context
	
	MediaReorgService mrService = new MediaReorgService()
	
	def MediaReorgController(AppContext context) {
		this.context = context
	}
	
	def execute() {
		context.appMediaList = mrService.findAllFiles(context.folderPath)
		println "Number of AppMedia found: ${context.appMediaList.size()}"
		println ''
		
		//augment AppMedia with creationDate, if RenameMethod.CreationDate
		if (context.renameMethod == RenameMethod.CreationDate) {
			context.appMediaList.each {am ->
				am.creationDate = mrService.findCreationDate(am.file, am.media)
			}
		}
		else if (context.renameMethod == RenameMethod.LastModified) {
			context.appMediaList.each {am ->
				am.lastModified = mrService.findLastModified(am.file)
			}
		} 

		//buildNewFilename
		context.appMediaList.each {am ->
			am.newFilename = mrService.buildNewFilename(am, context)
		}
		
		//Action.Report
		if (context.action == Action.Report) {
			mrService.report(context)
			println ''
		}
		else {	//Action.Rename
			context.appMediaList.each {am ->
				if (am.newFilename) {
					mrService.renameFile(am.file, am.newFilename)
				}
			}
		}

		println ''
	}
}
