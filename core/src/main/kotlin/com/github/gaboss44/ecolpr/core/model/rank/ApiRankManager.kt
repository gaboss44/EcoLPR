package com.github.gaboss44.ecolpr.core.model.rank

import com.github.gaboss44.ecolpr.api.model.rank.RankManager

class ApiRankManager() : RankManager {

    override fun getRank(name: String) = Ranks.getByID(name)?.proxy

    override fun getRankByGroup(group: String) = Ranks.getByGroup(group)?.proxy

    override fun getLoadedRanks() = Ranks.proxyValues()
}