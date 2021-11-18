package com.wadhams.media.reorg.type

enum Action {
	Report('REPORT'),
	Rename('RENAME'),
	Unknown('Unknown');
	
	private static EnumSet<Action> allEnums = EnumSet.allOf(Action.class)
	
	private final String name
	
	Action(String name) {
		this.name = name
	}
	
	public static Action findByName(String text) {
		if (text) {
			text = text.toUpperCase()
			for (Action e : allEnums) {
				if (e.name.equals(text)) {
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
