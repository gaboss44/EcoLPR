package com.github.gaboss44.ecolpr.api.model.rank

interface RankManager {

    fun getRank(name: String) : Rank?

    fun getRankByGroup(group: String) : Rank?

    fun getLoadedRanks() : Set<Rank>

}