package com.wadhams.media.reorg.app

import com.wadhams.media.reorg.context.AppContext
import com.wadhams.media.reorg.controller.MediaReorgController
import com.wadhams.media.reorg.type.Action

class MainApp {
    static void main(String[] args) {
		println 'MainApp started...'
		println ''

		if (args.size() > 1) {
			AppContext context = new AppContext()
			context.action = Action.findByName(args[0])
			println "Action........: ${context.action}"
			context.folderPath = args[1]
			println "Folder path...: ${context.folderPath}"
			if (args.size() > 2) {	//groupName
				context.groupName = args[2]
				println "Group name....: ${context.groupName}"
			}
			if (args.size() > 3) {	//groupName
				context.groupDate = args[3]
				println "Group date....: ${context.groupDate}"
			}
			println ''
			
			if (context.action != Action.Unknown) {
				MainApp app = new MainApp()
				app.execute(context)
			}
			else {
				println 'Unknown parameter. Application did not run.'
				println ''
			}
		}
		else {
			println 'Invalid number of arguments. Application did not run.'
			println ''
		}

		println 'MainApp ended.'
    }
	
	def execute(AppContext context) {
		MediaReorgController controller = new MediaReorgController(context)
		controller.execute()
	}

}
