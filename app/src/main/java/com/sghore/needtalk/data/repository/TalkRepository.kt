package com.sghore.needtalk.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.sghore.needtalk.data.model.entity.TalkHighlightEntity
import com.sghore.needtalk.data.model.entity.TalkHistoryEntity
import com.sghore.needtalk.data.model.entity.TalkHistoryParticipantEntity
import com.sghore.needtalk.data.model.entity.TalkSettingEntity
import com.sghore.needtalk.data.repository.database.TalkDao
import com.sghore.needtalk.data.repository.database.UserDao
import com.sghore.needtalk.data.repository.datasource.GetTalkHistoryPagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TalkRepository @Inject constructor(
    private val talkDao: TalkDao,
    private val userDao: UserDao
) {
    fun getTalkHistoryEntity(id: String) =
        talkDao.getTalkHistory(id)

    fun getTalkHistoryEntities(offset: Int, limit: Int = 20) =
        talkDao.getTalkHistoryEntities(offset, limit)

    // 대화기록 페이징 하여 반환
    fun getPagingTalkHistory(pageSize: Int = 20) = Pager(PagingConfig(pageSize)) {
        GetTalkHistoryPagingSource(talkDao, userDao, pageSize)
    }.flow

    fun getTalkHistoryParticipantEntities(id: String) =
        talkDao.getTalkHistoryParticipantEntities(id)

    fun getTalkHighlightEntities(talkHistoryId: String) =
        talkDao.getTalkHighlightEntities(talkHistoryId)

    // 대화 기록 저장
    suspend fun insertTalkHistoryEntity(talkHistoryEntity: TalkHistoryEntity) {
        talkDao.insertTalkHistoryEntity(talkHistoryEntity)
    }

    // 대화에 참여한 참여자들에 정보 저장
    suspend fun insertTalkHistoryParticipantEntity(
        talkHistoryParticipantEntity: TalkHistoryParticipantEntity
    ) {
        talkDao.insertTalkHistoryParticipantEntity(talkHistoryParticipantEntity)
    }

    // 대화 하이라이트 정보 저장
    suspend fun insertTalkHighlightEntity(talkHighlightEntity: TalkHighlightEntity) {
        talkDao.insertTalkHighlightEntity(talkHighlightEntity)
    }

    // 대화기록 삭제
    suspend fun deleteTalkHistoryEntity(id: String) = talkDao.deleteTalkHistoryEntity(id)

    suspend fun deleteTalkHighlightEntity(id: Int?) = talkDao.deleteTalkHighlightEntity(id)

    suspend fun deleteTalkHistoryParticipantEntities(id: String) =
        talkDao.deleteTalkHistoryParticipantEntities(id)

    // 저장된 대화방 설정 가져옴
    fun getTalkSettingEntity() = talkDao.getTalkSettingEntity()

    // 대화방 설정 저장
    suspend fun insertTalkSettingEntity(talkSettingEntity: TalkSettingEntity) =
        talkDao.insertTalkSettingEntity(talkSettingEntity)
}