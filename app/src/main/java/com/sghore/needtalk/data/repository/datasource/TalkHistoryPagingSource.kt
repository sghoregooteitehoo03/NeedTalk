package com.sghore.needtalk.data.repository.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sghore.needtalk.data.repository.database.TalkDao
import com.sghore.needtalk.domain.model.TalkHistory
import kotlinx.coroutines.flow.first

class TalkHistoryPagingSource(private val talkDao: TalkDao) : PagingSource<Int, TalkHistory>() {
    override fun getRefreshKey(state: PagingState<Int, TalkHistory>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TalkHistory> {
        return try {
            val offset = params.key ?: 0
            val talkEntity = talkDao.getTalkEntity(offset).first()
            val talkHistory = talkEntity.map {
                val users = it.usersId.split(",").map { userId ->
                    talkDao.getUserEntity(userId.trim()).first()
                }

                TalkHistory(
                    talkTime = it.talkTime,
                    users = users,
                    createTimeStamp = it.createTimeStamp
                )
            }

            LoadResult.Page(
                data = talkHistory,
                prevKey = null,
                nextKey = if (talkEntity.isNotEmpty()) {
                    offset + 5
                } else {
                    null
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}