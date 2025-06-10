package com.github.gaboss44.ecolpr.transition

class RankTransitionResultImpl<T : RankTransition>(
    override val transition: T,
    override val wasCancelled: Boolean
) : RankTransitionResult<T>