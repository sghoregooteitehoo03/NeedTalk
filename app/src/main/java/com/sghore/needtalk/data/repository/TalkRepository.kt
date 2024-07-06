package com.sghore.needtalk.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.sghore.needtalk.data.model.entity.TalkEntity
import com.sghore.needtalk.data.model.entity.TalkHistoryEntity
import com.sghore.needtalk.data.model.entity.TalkHistoryParticipantEntity
import com.sghore.needtalk.data.model.entity.TalkSettingEntity
import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.data.repository.database.TalkDao
import com.sghore.needtalk.data.repository.database.UserDao
import com.sghore.needtalk.data.repository.datasource.GetTalkHistoryPagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TalkRepository @Inject constructor(
    private val talkDao: TalkDao,
    private val userDao: UserDao
) {

    // 유저 아이디를 통해 유저 정보를 반환함
    fun getUserEntity(userId: String) = talkDao.getUserEntity(userId)

    // 유저 정보 삽입
    suspend fun insertUserEntity(userEntity: UserEntity) {
        talkDao.insertUserEntity(userEntity)
    }

    // 대화 주제 리스트 반환
    fun getTalkTopicEntity(groupCode: Int) = talkDao.getTalkTopicEntity(groupCode)

    // 대화 기록 삽입
    suspend fun insertTalkEntity(talkEntity: TalkEntity) {
        talkDao.insertTalkEntity(talkEntity)
    }

    fun getTalkHistoryEntities(offset: Int, limit: Int = 20) =
        talkDao.getTalkHistoryEntities(offset, limit)

    // 대화기록 페이징 하여 반환
    fun getPagingTalkHistory(pageSize: Int = 20) = Pager(PagingConfig(pageSize)) {
        GetTalkHistoryPagingSource(talkDao, userDao, pageSize)
    }.flow

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

    // 저장된 대화방 설정 가져옴
    fun getTalkSettingEntity() = talkDao.getTalkSettingEntity()

    // 대화방 설정 저장
    suspend fun insertTalkSettingEntity(talkSettingEntity: TalkSettingEntity) =
        talkDao.insertTalkSettingEntity(talkSettingEntity)
}