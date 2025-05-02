let csrfToken;
const alerPlate = document.getElementById('successAlert');
const timerLine = document.querySelector('.timer-line');
const alertMsg = document.getElementById('alertMsg');
const tableBody = document.getElementById('users-table-body');

// ==================== НОВЫЙ СПОСОБ ПОЛУЧЕНИЯ CSRF ТОКЕНА ====================
function getCsrfTokenFromCookies() {
    const cookies = document.cookie.split(';');
    for (let cookie of cookies) {
        const [name, value] = cookie.trim().split('=');
        if (name === 'XSRF-TOKEN') {
            return decodeURIComponent(value);
        }
    }
    return null;
}

async function fetchCsrfToken() {
    try {
        // Пробуем получить токен из кук
        csrfToken = getCsrfTokenFromCookies();

        // Если в куках нет, запрашиваем с сервера
        if (!csrfToken) {
            const response = await fetch('/api/admin/sendCSRF', {
                credentials: 'include'
            });

            if (!response.ok) {
                throw new Error('CSRF token request failed');
            }

            const data = await response.json();
            csrfToken = data.token;
        }

        console.log('CSRF токен получен:', csrfToken);
        return csrfToken;
    } catch (error) {
        console.error('Ошибка получения CSRF токена:', error);
        throw error;
    }
}

//ОБЩАЯ ФУНКЦИЯ ДЛЯ ЗАПРОСОВ
async function makeAuthorizedRequest(url, method, data = null) {
    try {
        // Получаем свежий CSRF токен перед каждым запросом
        await fetchCsrfToken();

        const headers = {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': csrfToken  // Важно: используем X-XSRF-TOKEN
        };

        const config = {
            method: method,
            headers: headers,
            credentials: 'include'  // Обязательно для работы с куками
        };

        if (data) {
            config.body = JSON.stringify(data);
        }

        const response = await fetch(url, config);

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || `Ошибка запроса: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error(`Ошибка в запросе ${method} ${url}:`, error);
        throw error;
    }
}

//ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ
function showSuccess(msg) {
    if (!alerPlate || !timerLine || !alertMsg) return;

    timerLine.style.width = '0%';
    alerPlate.classList.remove('hidden');
    alerPlate.classList.add('active');
    alertMsg.textContent = msg;

    setTimeout(() => {
        timerLine.style.width = '100%';
    }, 200);
}

function hideSuccess() {
    if (!alerPlate || !timerLine) return;

    timerLine.style.width = '0%';
    setTimeout(() => {
        alerPlate.classList.remove('active');
        alerPlate.classList.add('hidden');
    }, 5000);
}

function anim(text) {
    showSuccess(text);
    setTimeout(hideSuccess, 1000);
}

// ОСНОВНЫЕ ФУНКЦИИ
async function refreshData() {
    try {
        await getUsersList();
        await getAuthorizedUser();
    } catch (error) {
        console.error('Ошибка обновления данных:', error);
    }
}

async function getUsersList() {
    try {
        const users = await makeAuthorizedRequest('/api/admin/users', 'GET');
        renderUsersTable(users);
    } catch (error) {
        anim('Ошибка загрузки пользователей');
        console.error('Ошибка getUsersList:', error);
    }
}

function renderUsersTable(users) {
    if (!tableBody) return;

    let rowsHtml = '';
    users.forEach(user => {
        const roleNames = user.roles?.map(r => r.replace('ROLE_', '')).join(' ') || '';
        rowsHtml += `
            <tr class="alert-secondary">
                <td class="py-2 pl-2">${user.id}</td>
                <td>${user.name}</td>
                <td>${user.email}</td>
                <td>${roleNames}</td>
                <td class="py-2">
                    <button class="btn btn-info open-edit-modal"
                            data-toggle="modal"
                            data-target="#editUserModal"
                            data-id="${user.id}"
                            data-name="${user.name}"
                            data-email="${user.email}"
                            data-roles="${roleNames}">Edit</button>
                </td>
                <td class="py-2">
                    <button class="btn btn-danger open-delete-modal"
                            data-toggle="modal"
                            data-target="#deleteUserModal"
                            data-id="${user.id}"
                            data-name="${user.name}"
                            data-email="${user.email}"
                            data-roles="${roleNames}">Delete</button>
                </td>
            </tr>
        `;
    });

    tableBody.innerHTML = rowsHtml;
}

async function getAuthorizedUser() {
    try {
        const user = await makeAuthorizedRequest('/api/admin/authorizedUser', 'GET');
        updateUserInfoUI(user);
    } catch (error) {
        console.error('Ошибка getAuthorizedUser:', error);
    }
}

function updateUserInfoUI(user) {
    const headerEmail = document.getElementById('userDataEmail');
    const headerRoles = document.getElementById('userDataRoles');
    const authorizedUserData = document.getElementById('authorizedUserData');

    if (!headerEmail || !headerRoles || !authorizedUserData) return;

    const roleNames = user.roles?.map(role => role.replace('ROLE_', '')).join(' ') || '';

    headerEmail.textContent = user.email;
    headerRoles.textContent = roleNames;

    authorizedUserData.innerHTML = `
        <tr class="border-top alert-secondary">
            <td class="py-2 pl-2">${user.id}</td>
            <td>${user.name}</td>
            <td>${user.email}</td>
            <td class="py-2">${roleNames}</td>
        </tr>
    `;
}

//  ОБРАБОТЧИКИ СОБЫТИЙ
async function initializeEventListeners() {
    // Форма добавления пользователя
    const addForm = document.getElementById('addNewUser');
    if (addForm) {
        addForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            const userData = {
                name: document.getElementById('nameToAdd').value.trim(),
                email: document.getElementById('emailToAdd').value.trim(),
                password: document.getElementById('passwordToAdd').value,
                roles: Array.from(document.querySelectorAll('input[name="roleToAdd"]:checked'))
                    .map(cb => cb.value)
            };

            if (!validateUserData(userData)) return;

            try {
                await makeAuthorizedRequest('/api/admin/users/create-data', 'POST', userData);
                anim('Пользователь успешно добавлен!');
                refreshData();
                addForm.reset();

                // Переключение на таблицу
                document.getElementById('usersList').style.display = 'block';
                document.getElementById('addUserForm').style.display = 'none';
            } catch (error) {
                anim(error.message || 'Ошибка добавления пользователя');
            }
        });
    }

    // Форма редактирования пользователя
    const editForm = document.getElementById('editUserForm');
    if (editForm) {
        editForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            const userData = {
                id: document.getElementById('editUserId').value,
                name: document.getElementById('editName').value,
                email: document.getElementById('editEmail').value,
                password: document.getElementById('editPassword').value,
                roles: Array.from(document.querySelectorAll('input[name="roleToEdit"]:checked'))
                    .map(cb => cb.value)
            };

            try {
                await makeAuthorizedRequest(`/api/admin/users/${userData.id}/update-data`, 'PUT', userData);
                anim('Пользователь успешно обновлен!');
                refreshData();
                $('#editUserModal').modal('hide');
            } catch (error) {
                anim(error.message || 'Ошибка обновления пользователя');
            }
        });
    }

    // Обработчик кнопки удаления
    document.addEventListener('click', async (e) => {
        if (e.target.classList.contains('open-delete-modal')) {
            const userId = e.target.dataset.id;
            const userName = e.target.dataset.name;

            // Заполняем модальное окно данными
            document.getElementById('deleteUserId').textContent = userId;
            document.getElementById('deleteName').textContent = userName;
            document.getElementById('deleteEmail').textContent = e.target.dataset.email;
            document.getElementById('deleteRoles').textContent = e.target.dataset.roles;

            // Показываем наше модальное окно
            $('#deleteUserModal').modal('show');
        }
    });

// Обработчик подтверждения удаления
    document.getElementById('confirmDeleteBtn').addEventListener('click', async () => {
        const userId = document.getElementById('deleteUserId').textContent;

        try {
            const response = await fetch(`/api/admin/users/${userId}`, {
                method: 'DELETE',
                headers: {
                    'X-XSRF-TOKEN': csrfToken,
                    'Content-Type': 'application/json'
                },
                credentials: 'include'
            });

            if (!response.ok) {
                // Пытаемся прочитать текст ошибки, если есть
                const errorText = await response.text();
                throw new Error(errorText || `HTTP error! status: ${response.status}`);
            }

            anim('Пользователь успешно удален!');
            refreshData();
            $('#deleteUserModal').modal('hide');

        } catch (error) {
            console.error('Delete error:', error);
            anim(error.message || 'Ошибка удаления пользователя');
            $('#deleteUserModal').modal('hide');
        }
    });
}

function validateUserData(userData) {
    if (!userData.name || !userData.email || !userData.password) {
        anim('Заполните все обязательные поля');
        return false;
    }

    if (userData.roles.length === 0) {
        anim('Выберите хотя бы одну роль');
        return false;
    }

    return true;
}


document.addEventListener('DOMContentLoaded', async () => {
    try {
        // Инициализация CSRF токена
        await fetchCsrfToken();

        // Загрузка данных
        await refreshData();

        // Настройка обработчиков событий
        initializeEventListeners();
    } catch (error) {
        console.error('Ошибка инициализации:', error);
        anim('Ошибка загрузки приложения');
    }
});