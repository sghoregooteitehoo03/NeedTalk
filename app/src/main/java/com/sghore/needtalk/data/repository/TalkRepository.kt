package com.sghore.needtalk.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.sghore.needtalk.data.model.entity.TalkEntity
import com.sghore.needtalk.data.model.entity.TalkTopicEntity
import com.sghore.needtalk.data.model.entity.TimerSettingEntity
import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.data.repository.database.TalkDao
import com.sghore.needtalk.data.repository.datasource.TalkHistoryPagingSource
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

    // 대화 기록을 페이징 하여 가져옴
    fun getPagingTalkHistory() = Pager(config = PagingConfig(10)) {
        TalkHistoryPagingSource(dao)
    }.flow

    // 시작 날짜와 끝 날짜까지의 모든 대화기록을 가져옴
    suspend fun getTalkEntities(startTime: Long, endTime: Long) =
        dao.getTalkEntity(startTime, endTime)

    // 대화 통계 데이터 반환
    suspend fun getTalkStatics(
        currentUser: UserEntity,
        talkHistory: List<TalkHistory>,
        count: Int
    ): TalkStatics {
        var totalTime = 0L // 총 대화 시간
        val dayOfWeekData = mutableListOf(-1L, 0L, 0L, 0L, 0L, 0L, 0L, 0L) // 요일별 대화 시간
        val participantMap = mutableMapOf<String, ParticipantCount>() // 주로 함께한 인원
        val numberData = mutableListOf(0, 0, 0) // 인원 수 비율
        val calendar = Calendar.getInstance()

        talkHistory.forEach {
            calendar.timeInMillis = it.createTimeStamp

            // 다른 유저와 함께한 횟수를 카운팅 합니다.
            it.users.forEach { user ->
                if (user != null) {
                    if (currentUser.userId != (user.userId)) {
                        val previousData = participantMap[user.userId] ?: ParticipantCount(user, 0)

                        participantMap[user.userId] =
                            previousData.copy(count = previousData.count + 1)
                    }
                }
            }

            totalTime += it.talkTime
            dayOfWeekData[calendar.get(Calendar.DAY_OF_WEEK)] += it.talkTime
            numberData[it.users.size - 2] += 1
        }

        val participantList = participantMap.values
            .toList()
            .sortedByDescending { it.count }
            .map {
                val loadUserData = getUserEntity(it.userEntity.userId).first()
                if (loadUserData != null) {
                    it.copy(userEntity = loadUserData)
                } else {
                    it
                }
            }
        dayOfWeekData.removeAt(0)

        return TalkStatics(
            totalTalkTime = totalTime,
            dayOfWeekTalkTime = dayOfWeekData,
            participantCount = if (participantList.size < count)
                participantList.subList(0, participantList.size)
            else
                participantList.subList(0, count),
            numberOfPeopleRate = numberData.map {
                if (it == 0)
                    0f
                else
                    ((it.toFloat() / talkHistory.size) * 100).roundToInt().toFloat()
            }
        )
    }

    // 대화 주제 리스트 반환
    fun getTalkTopicEntity(groupCode: Int) = dao.getTalkTopicEntity(groupCode)

    // 대화 주제 삽입
    suspend fun insertTalkTopic(talkTopicEntity: TalkTopicEntity) =
        dao.insertTalkTopicEntity(talkTopicEntity)

    // 대화 주제 삭제
    suspend fun deleteTalkTopic(talkTopicEntity: TalkTopicEntity) =
        dao.deleteTalkTopicEntity(talkTopicEntity)

    // 대화 기록 삽입
    suspend fun insertTalkEntity(talkEntity: TalkEntity) {
        dao.insertTalkEntity(talkEntity)
    }

    // 저장된 타이머 설정 반환
    fun getTimerSettingEntity() =
        dao.getTimerSettingEntity()

    // 타이머 설정 삽입
    suspend fun insertTimerSettingEntity(timerSettingEntity: TimerSettingEntity) =
        dao.insertTimerSettingEntity(timerSettingEntity)
}