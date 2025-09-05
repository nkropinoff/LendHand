// src/main/resources/static/js/profile.js
document.addEventListener('DOMContentLoaded', () => {
    const editSection = document.getElementById('profile-edit-section');
    if (!editSection) return;

    const editBtn = document.getElementById('edit-profile-btn');
    const cancelBtn = document.getElementById('cancel-edit-btn');
    const viewMode = document.getElementById('view-mode');
    const editForm = document.getElementById('edit-form');

    const toggleEditMode = (isEdit) => {
        if (isEdit) {
            viewMode.style.display = 'none';
            editForm.style.display = 'block';
            editBtn.style.display = 'none';
        } else {
            viewMode.style.display = 'block';
            editForm.style.display = 'none';
            editBtn.style.display = 'block';
        }
    };

    editBtn.addEventListener('click', () => {
        toggleEditMode(true);
    });

    cancelBtn.addEventListener('click', () => {
        // Можно добавить сброс полей формы к исходным значениям,
        // но для простоты просто скрываем форму.
        // При перезагрузке страницы данные и так будут актуальными.
        toggleEditMode(false);
    });
});
