package com.sghore.needtalk.data.repository.datasource

import androidx.compose.ui.graphics.asImageBitmap
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sghore.needtalk.data.repository.database.TalkDao
import com.sghore.needtalk.data.repository.database.UserDao
import com.sghore.needtalk.domain.model.TalkHistory
import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.util.byteArrayToBitmap
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class GetTalkHistoryPagingSource(
    private val talkDao: TalkDao,
    private val userDao: UserDao,
    private val pageSize: Int
) : PagingSource<Int, TalkHistory>() {
    override fun getRefreshKey(state: PagingState<Int, TalkHistory>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TalkHistory> {
        return try {
            val offset = params.key ?: 0
            // TODO: JOIN 방식으로 변경
            val talkHistoryEntities =
                talkDao.getTalkHistoryEntities(offset = offset, limit = pageSize).first()

            if (talkHistoryEntities.isEmpty())
                throw NullPointerException()

            val talkHistoryList = talkHistoryEntities.map { talkHistoryEntity ->
                val users = talkDao.getTalkHistoryParticipantEntities(talkHistoryEntity.id)
                    .first()
                    .map { participantEntity ->
                        val userEntity = userDao.getUserEntity(participantEntity.userId).first()!!
                        UserData(
                            userId = userEntity.userId,
                            name = userEntity.name,
                            profileImage = byteArrayToBitmap(userEntity.profileImage)
                                .asImageBitmap(),
                            experiencePoint = 0f,
                            friendshipPoint = participantEntity.friendshipPoint
                        )
                    }

                TalkHistory(
                    id = talkHistoryEntity.id,
                    talkTitle = talkHistoryEntity.talkTitle,
                    talkTime = talkHistoryEntity.talkTime,
                    recordFilePath = talkHistoryEntity.recordFilePath,
                    recordFileSize = talkHistoryEntity.recordFileSize,
                    users = users,
                    clipCount = 0,
                    createTimeStamp = talkHistoryEntity.createTimeStamp
                )
            }

            LoadResult.Page(
                data = talkHistoryList,
                prevKey = null,
                nextKey = offset + pageSize
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}