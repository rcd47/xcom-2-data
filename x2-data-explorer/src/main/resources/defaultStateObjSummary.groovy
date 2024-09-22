// mods frequently add their own object types, and we don't know the inheritance hierarchy
// so we try to match by looking at the shape of an object rather than explicit types

// XComGameState_Ability, XComGameState_Item, and their subclasses
if (gso.m_TemplateName && gso.OwnerStateObject) {
	return gso.m_TemplateName.value.original + ' for ' + gso.OwnerStateObject.ObjectID.value
}

// XComGameState_Effect and subclasses
if (gso.ApplyEffectParameters) {
	return gso.ApplyEffectParameters.EffectRef.SourceTemplateName.value.original +
			' by ' +
			gso.ApplyEffectParameters.SourceStateObjectRef.ObjectID.value +
			' on ' +
			gso.ApplyEffectParameters.TargetStateObjectRef.ObjectID.value
}

// XComGameState_Unit
if (gso.strFirstName || gso.strLastName) {
	return gso.strFirstName?.value + ' ' + gso.strLastName?.value
} else if (gso.m_SoldierClassTemplateName) {
	return gso.m_SoldierClassTemplateName.value.original
}

// XComGameState_Player
if (gso.TeamFlag) {
	return gso.TeamFlag.value.original
}

// fallback. lots of the stock objects have m_TemplateName.
if (gso.m_TemplateName) {
	return gso.m_TemplateName.value.original
}
