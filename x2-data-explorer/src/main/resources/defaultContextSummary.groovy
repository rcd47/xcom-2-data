// mods could add their own context types, and we don't know the inheritance hierarchy
// so we try to match by looking at the shape of the context rather than explicit types

// XComGameStateContext_ChangeContainer
if (ctx.ChangeInfo) {
	return ctx.ChangeInfo
}

if (ctx.InputContext) {
	// XComGameStateContext_Ability
	if (ctx.ResultContext) {
		return ctx.InputContext.AbilityTemplateName.original
	}
	
	// XComGameStateContext_HeadquartersOrder
	if (ctx.InputContext.OrderType) {
		return ctx.InputContext.OrderType.original
	}
}

// XComGameStateContext_Kismet
if (ctx.SeqOpName) {
	return ctx.SeqOpName.original
}

// XComGameStateContext_WillRoll
if (ctx.RollSourceFriendly) {
	return ctx.TargetUnitID + ' ' + ctx.RollSourceFriendly
}

// XComGameStateContext_StrategyGameRule and XComGameStateContext_TacticalGameRule
if (ctx.GameRuleType) {
	return ctx.GameRuleType.original
}
