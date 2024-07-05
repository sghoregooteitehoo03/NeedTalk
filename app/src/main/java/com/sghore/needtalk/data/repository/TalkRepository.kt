package com.sghore.needtalk.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.sghore.needtalk.data.model.entity.TalkEntity
import com.sghore.needtalk.data.model.entity.TalkHistoryEntity
import com.sghore.needtalk.data.model.entity.TalkHistoryParticipantEntity
import com.sghore.needtalk.data.model.entity.TalkSettingEntity
import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.data.repository.database.TalkDao
import com.sghore.needtalk.domain.model.ParticipantCount
import com.sghore.needtalk.domain.model.TalkHistory
import com.sghore.needtalk.domain.model.TalkStatics
import kotlinx.coroutines.flow.first
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.roundToInt

class TalkRepository @Inject constructor(
    private val dao: TalkDao
) {

    // 유저 아이디를 통해 유저 정보를 반환함
    fun getUserEntity(userId: String) = dao.getUserEntity(userId)

    // 유저 정보 삽입
    suspend fun insertUserEntity(userEntity: UserEntity) {
        dao.insertUserEntity(userEntity)
    }

    // 시작 날짜와 끝 날짜까지의 모든 대화기록을 가져옴
    suspend fun getTalkEntities(startTime: Long, endTime: Long) =
        dao.getTalkEntity(startTime, endTime)

    // 대화 주제 리스트 반환
    fun getTalkTopicEntity(groupCode: Int) = dao.getTalkTopicEntity(groupCode)

    // 대화 기록 삽입
    suspend fun insertTalkEntity(talkEntity: TalkEntity) {
        dao.insertTalkEntity(talkEntity)
    }


    // 대화 기록 저장
    suspend fun insertTalkHistoryEntity(talkHistoryEntity: TalkHistoryEntity) {
        dao.insertTalkHistoryEntity(talkHistoryEntity)
    }

    // 대화에 참여한 참여자들에 정보 저장
    suspend fun insertTalkHistoryParticipantEntity(
        talkHistoryParticipantEntity: TalkHistoryParticipantEntity
    ) {
        dao.insertTalkHistoryParticipantEntity(talkHistoryParticipantEntity)
    }

    // 저장된 대화방 설정 가져옴
    fun getTalkSettingEntity() = dao.getTalkSettingEntity()

    // 대화방 설정 저장
    suspend fun insertTalkSettingEntity(talkSettingEntity: TalkSettingEntity) =
        dao.insertTalkSettingEntity(talkSettingEntity)
}