package com.wadhams.media.reorg.app

import com.wadhams.media.reorg.context.AppContext
import com.wadhams.media.reorg.controller.MediaReorgController
import com.wadhams.media.reorg.type.Action
import com.wadhams.media.reorg.type.RenameMethod

class MediaReorgApp {
    static void main(String[] args) {
		println 'MediaReorgApp started...'
		println ''
		println 'Usage: MediaReorgApp <action> <renameMethod> <folderPath> <groupName> <groupDate>'
		println '<action> = REPORT | rep | RENAME | ren'
		println '<renameMethod> = CREATION_DATE | cd | TIMESTAMP | ts | LAST_MODIFIED | lm'
		println "\tCREATION_DATE will try to use the creation date of the media in the new filename"
		println "\tLAST_MODIFIED will try to use the last modified date of the media in the new filename"
		println "\tTIMESTAMP will use the <groupDate> in the filename ignoring creation date and last modified completely"
		println '<groupDate> is only required when using a renameMethod of TIMESTAMP'
		println ''
		
		if (args.size() == 4 || args.size() == 5) {
			AppContext context = new AppContext()
			context.action = Action.findByName(args[0])
			println "Action..........: ${context.action}"
			context.renameMethod = RenameMethod.findByName(args[1])
			println "Rename Method...: ${context.renameMethod}"
			context.folderPath = args[2]
			println "Folder path.....: ${context.folderPath}"
			context.groupName = args[3]
			println "Group name......: ${context.groupName}"
			if (args.size() == 5) {
				context.groupDate = args[4]
				println "Group date......: ${context.groupDate}"
			}
			println ''

			if (context.action == Action.Unknown || context.renameMethod == RenameMethod.Unknown) {
				println "Unknown \'action\' parameter: ${args[0]}"
				println "\tOR"
				println "Unknown \'renameMethod\' parameter: ${args[1]}"
				println ''
				println 'See \'Usage\' above. Application did not run.'
				println ''
			}
			else {
				MediaReorgApp app = new MediaReorgApp()
				app.execute(context)
			}
		}
		else {
			println "Invalid number of arguments. args.size(): ${args.size()}"
			println 'See \'Usage\' above. Application did not run.'
			println ''
		}

		println 'MediaReorgApp ended.'
    }
	
	def execute(AppContext context) {
		MediaReorgController controller = new MediaReorgController(context)
		controller.execute()
	}

}
