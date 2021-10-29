package com.wadhams.media.reorg.type

enum Action {
	Rename('RENAME'),
	Review('REVIEW'),
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
			println ''
			return Action.Unknown
		}
		
		println "Unknown action text: $text"
		println ''
		return Action.Unknown
	}

}
