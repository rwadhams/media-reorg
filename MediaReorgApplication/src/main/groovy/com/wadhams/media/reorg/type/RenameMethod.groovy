package com.wadhams.media.reorg.type

enum RenameMethod {
	CreationDate(['CREATION_DATE', 'CD']),
	Timestamp(['TIMESTAMP', 'TS']),
	Unknown(['Unknown']);
	
	private static EnumSet<RenameMethod> allEnums = EnumSet.allOf(RenameMethod.class)

	private final List<String> names
	
	RenameMethod(List<String> names) {
		this.names = names
	}
	
	public static RenameMethod findByName(String text) {
		if (text) {
			text = text.toUpperCase()
			for (RenameMethod e : allEnums) {
				if (e.names.contains(text)) {
					return e
				}
			}
		}
		else {
			println 'findByName() was passed a blank or null name'
			return RenameMethod.Unknown
		}
		
		println "Unknown \'Rename Method\' lookup text: $text"
		return RenameMethod.Unknown
	}

}
