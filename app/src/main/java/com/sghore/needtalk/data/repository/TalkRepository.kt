package com.sghore.needtalk.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.sghore.needtalk.data.model.entity.TalkEntity
import com.sghore.needtalk.data.model.entity.TimerSettingEntity
import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.data.repository.database.TalkDao
import com.sghore.needtalk.data.repository.datasource.TalkHistoryPagingSource
import javax.inject.Inject

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