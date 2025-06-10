package com.github.gaboss44.ecolpr.transition

interface RankTransitionResult<T : RankTransition>{
    val transition: T
    val wasCancelled: Boolean
}