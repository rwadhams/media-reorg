package com.wadhams.media.reorg.context

import com.wadhams.media.reorg.dto.AppMedia
import com.wadhams.media.reorg.type.Action

class AppContext {
	Action action
	String folderPath
	String groupName
	String groupDate
	
	List<AppMedia> appMediaList
}
