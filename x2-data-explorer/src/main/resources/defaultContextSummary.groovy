// mods could add their own context types, and we don't know the inheritance hierarchy
// so we try to match by looking at the shape of the context rather than explicit types

// XComGameStateContext_ChangeContainer
if (ctx.ChangeInfo) {
	return ctx.ChangeInfo.value
}

// XComGameStateContext_Ability
if (ctx.InputContext) {
	return ctx.InputContext.AbilityTemplateName.value.original
}

// XComGameStateContext_Kismet
if (ctx.SeqOpName) {
	return ctx.SeqOpName.value.original
}

// XComGameStateContext_WillRoll
if (ctx.RollSourceFriendly) {
	return ctx.TargetUnitID.value + ' ' + ctx.RollSourceFriendly.value
}

// XComGameStateContext_StrategyGameRule and XComGameStateContext_TacticalGameRule
if (ctx.GameRuleType) {
	return ctx.GameRuleType.value.original
}
