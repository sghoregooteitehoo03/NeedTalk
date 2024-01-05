package com.sghore.needtalk.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.sghore.needtalk.data.model.UserEntity
import com.sghore.needtalk.data.repository.database.TalkDao
import com.sghore.needtalk.data.repository.datasource.TalkHistoryPagingSource
import javax.inject.Inject

class TalkRepository @Inject constructor(
    private val dao: TalkDao
) {

    fun getUserEntity(userId: String) = dao.getUserEntity(userId)

    suspend fun insertUserEntity(userEntity: UserEntity) = dao.insertUserEntity(userEntity)

    fun getPagingTalkHistory() = Pager(config = PagingConfig(10)) {
        TalkHistoryPagingSource(dao)
    }
}