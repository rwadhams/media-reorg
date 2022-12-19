package com.wadhams.media.reorg.type

enum Action {
	Report(['REPORT', 'REP']),
	Rename(['RENAME', 'REN']),
	Unknown(['Unknown']);
	
	private static EnumSet<Action> allEnums = EnumSet.allOf(Action.class)
	
	private final List<String> names
	
	Action(List<String> names) {
		this.names = names
	}
	
	public static Action findByName(String text) {
		if (text) {
			text = text.toUpperCase()
			for (Action e : allEnums) {
				if (e.names.contains(text)) {
					return e
				}
			}
		}
		else {
			println 'findByName() was passed a blank or null name'
			return Action.Unknown
		}
		
		println "Unknown \'Action\' lookup text: $text"
		return Action.Unknown
	}

}
