// mods frequently add their own object types, and we don't know the inheritance hierarchy
// so we try to match by looking at the shape of an object rather than explicit types

// XComGameState_Ability, XComGameState_Item, and their subclasses
if (gso.m_TemplateName && gso.OwnerStateObject) {
	return gso.m_TemplateName.original + ' for ' + gso.OwnerStateObject.ObjectID
}

// XComGameState_Effect and subclasses
if (gso.ApplyEffectParameters) {
	def source;
	if (gso.ApplyEffectParameters.EffectRef.SourceTemplateName) {
		source = gso.ApplyEffectParameters.EffectRef.SourceTemplateName.original
	} else {
		source = gso.ApplyEffectParameters.EffectRef.LookupType.original
	}
	if (gso.ApplyEffectParameters.SourceStateObjectRef) { // effects applied by XCGSC_UpdateWorldEffects do not have a source
		source += ' by ' + gso.ApplyEffectParameters.SourceStateObjectRef.ObjectID
	}
	return source + ' on ' + gso.ApplyEffectParameters.TargetStateObjectRef.ObjectID
}

// XComGameState_Unit
if (gso.strFirstName || gso.strLastName) {
	return gso.strFirstName + ' ' + gso.strLastName
} else if (gso.m_SoldierClassTemplateName) {
	return gso.m_SoldierClassTemplateName.original
}

// XComGameState_Player
if (gso.TeamFlag) {
	return gso.TeamFlag.original
}

// fallback. lots of the stock objects have m_TemplateName.
if (gso.m_TemplateName) {
	return gso.m_TemplateName.original
}
