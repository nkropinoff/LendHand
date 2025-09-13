document.addEventListener('DOMContentLoaded', () => {

    const editSection = document.getElementById('profile-edit-section');
    if (editSection) {
        const editBtn = document.getElementById('edit-profile-btn');
        const cancelBtn = document.getElementById('cancel-edit-btn');
        const viewMode = document.getElementById('view-mode');
        const editForm = document.getElementById('edit-form');

        const toggleEditMode = (isEdit) => {
            if (viewMode && editForm && editBtn) {
                viewMode.style.display = isEdit ? 'none' : 'block';
                editForm.style.display = isEdit ? 'block' : 'none';
                editBtn.style.display = isEdit ? 'none' : 'block';
            }
        };

        if(editBtn) {
            editBtn.addEventListener('click', () => toggleEditMode(true));
        }
        if(cancelBtn) {
            cancelBtn.addEventListener('click', () => toggleEditMode(false));
        }
    }

    const avatarFileInput = document.getElementById('avatarFile');
    const avatarPreview = document.querySelector('.profile-avatar');
    const saveAvatarBtn = document.getElementById('save-avatar-btn');

    if (avatarFileInput && avatarPreview && saveAvatarBtn) {
        avatarFileInput.addEventListener('change', (event) => {
            const file = event.target.files[0];
            if (file) {
                if (!file.type.startsWith('image/')){
                    alert('Пожалуйста, выберите файл изображения.');
                    return;
                }

                const reader = new FileReader();
                reader.onload = (e) => {
                    avatarPreview.style.backgroundImage = `url(${e.target.result})`;
                };
                reader.readAsDataURL(file);

                saveAvatarBtn.style.display = 'inline-block';
            } else {
                saveAvatarBtn.style.display = 'none';
            }
        });
    }
});
