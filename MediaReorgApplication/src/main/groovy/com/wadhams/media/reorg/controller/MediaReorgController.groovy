package com.wadhams.media.reorg.controller

import com.wadhams.media.reorg.context.AppContext
import com.wadhams.media.reorg.service.MediaReorgService
import com.wadhams.media.reorg.type.Action

class MediaReorgController {
	AppContext context
	
	MediaReorgService mrService = new MediaReorgService()
	
	def MediaReorgController(AppContext context) {
		this.context = context
	}
	
	def execute() {
		context.appMediaList = mrService.fileAllFiles(context.folderPath)
		println "Number of AppMedia found: ${context.appMediaList.size()}"
		println ''
		
		//augment AppMedia with creationDate
		context.appMediaList.each {am ->
			am.creationDate = mrService.findCreationDate(am.file, am.media)
		}

		mrService.report(context)
		println ''

		if (context.action == Action.Rename) {
			context.appMediaList.each {am ->
				String newFilename = mrService.buildNewFilename(am, context.groupName, context.groupDate)
				mrService.renameFile(am.file, newFilename)
			}
			println ''
		}
		
		context.appMediaList.each {am ->
			println am
		}
		println ''
	}
}
