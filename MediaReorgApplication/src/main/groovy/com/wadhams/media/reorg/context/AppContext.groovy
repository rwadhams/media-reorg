package com.wadhams.media.reorg.context

import com.wadhams.media.reorg.dto.AppMedia
import com.wadhams.media.reorg.type.Action
import com.wadhams.media.reorg.type.RenameMethod

class AppContext {
	Action action
	RenameMethod renameMethod
	String folderPath
	String groupName
	String groupDate
	
	List<AppMedia> appMediaList
}
