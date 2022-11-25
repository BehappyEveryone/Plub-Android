package com.plub.domain.repository

import com.plub.domain.model.vo.jwt_token.PlubJwtTokenVo
import kotlinx.coroutines.flow.Flow

interface PlubJwtTokenRepository {
    fun saveAccessToken(accessToken: String): Flow<Nothing>

    fun saveAccessTokenAndRefreshToken(accessToken: String, refreshToken: String): Flow<Nothing>

    fun getAccessToken(): Flow<String>

    fun getRefreshToken(): Flow<String>

    /**
     * 토큰 갱신 실패시 PlubJwtTokenData("", "")를 반환합니다.
     */
    fun reIssueToken(refreshToken : String): Flow<PlubJwtTokenVo>
}