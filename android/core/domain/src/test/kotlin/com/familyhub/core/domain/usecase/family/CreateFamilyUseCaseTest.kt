package com.familyhub.core.domain.usecase.family

import com.familyhub.core.common.exception.FamilyHubException
import com.familyhub.core.common.result.Result
import com.familyhub.core.domain.model.Family
import com.familyhub.core.domain.model.FamilyMember
import com.familyhub.core.domain.model.UserRole
import com.familyhub.core.domain.repository.FamilyRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * CreateFamilyUseCase Tests - TDD RED Phase
 */
class CreateFamilyUseCaseTest {

    private lateinit var familyRepository: FamilyRepository
    private lateinit var createFamilyUseCase: CreateFamilyUseCase

    private val mockFamily = Family(
        id = "family123",
        name = "Smith Family",
        description = "Our family",
        createdAt = System.currentTimeMillis(),
        createdBy = "user123",
        members = listOf(
            FamilyMember(
                uid = "user123",
                email = "test@example.com",
                displayName = "Test User",
                role = UserRole.ADMIN,
                joinedAt = System.currentTimeMillis()
            )
        )
    )

    @Before
    fun setup() {
        familyRepository = mockk()
        createFamilyUseCase = CreateFamilyUseCase(familyRepository)
    }

    @Test
    fun `invoke with valid name returns Success`() = runTest {
        // Given
        val name = "Smith Family"
        val description = "Our family"

        coEvery {
            familyRepository.createFamily(name, description)
        } returns Result.Success(mockFamily)

        // When
        val result = createFamilyUseCase(name, description)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val successResult = result as Result.Success
        assertThat(successResult.data).isEqualTo(mockFamily)

        coVerify(exactly = 1) {
            familyRepository.createFamily(name, description)
        }
    }

    @Test
    fun `invoke with empty name returns Error`() = runTest {
        // Given
        val name = ""
        val description = "Our family"

        // When
        val result = createFamilyUseCase(name, description)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isInstanceOf(FamilyHubException.ValidationException::class.java)
        assertThat(errorResult.exception.message).contains("Family name cannot be empty")

        coVerify(exactly = 0) {
            familyRepository.createFamily(any(), any())
        }
    }

    @Test
    fun `invoke with name too long returns Error`() = runTest {
        // Given
        val name = "A".repeat(101) // 101 characters
        val description = "Our family"

        // When
        val result = createFamilyUseCase(name, description)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isInstanceOf(FamilyHubException.ValidationException::class.java)
        assertThat(errorResult.exception.message).contains("Family name must be 100 characters or less")

        coVerify(exactly = 0) {
            familyRepository.createFamily(any(), any())
        }
    }

    @Test
    fun `invoke without description returns Success`() = runTest {
        // Given
        val name = "Smith Family"
        val description: String? = null

        coEvery {
            familyRepository.createFamily(name, description)
        } returns Result.Success(mockFamily)

        // When
        val result = createFamilyUseCase(name, description)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)

        coVerify(exactly = 1) {
            familyRepository.createFamily(name, description)
        }
    }

    @Test
    fun `invoke with network error returns Error`() = runTest {
        // Given
        val name = "Smith Family"
        val description = "Our family"

        coEvery {
            familyRepository.createFamily(name, description)
        } returns Result.Error(FamilyHubException.NetworkException())

        // When
        val result = createFamilyUseCase(name, description)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isInstanceOf(FamilyHubException.NetworkException::class.java)
    }
}
