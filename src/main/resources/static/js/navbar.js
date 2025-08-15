// Оборачиваем весь код в IIFE (Immediately Invoked Function Expression)
// Это хорошая практика, чтобы не загрязнять глобальное пространство имен.
(() => {
    // Используем 'DOMContentLoaded', чтобы убедиться, что весь HTML загружен
    // перед тем, как мы начнем искать элементы.
    document.addEventListener('DOMContentLoaded', () => {
        const menuButton = document.getElementById('user-menu-button');
        const dropdownMenu = document.getElementById('user-menu-dropdown');

        // Проверяем, что оба элемента существуют на странице
        if (!menuButton || !dropdownMenu) {
            return;
        }

        // Функция для переключения видимости меню
        const toggleMenu = (event) => {
            // event.stopPropagation() предотвращает немедленное закрытие меню,
            // так как клик по кнопке не "всплывет" до 'document'.
            event.stopPropagation();
            dropdownMenu.classList.toggle('active');
        };

        // Функция для закрытия меню
        const closeMenu = () => {
            if (dropdownMenu.classList.contains('active')) {
                dropdownMenu.classList.remove('active');
            }
        };

        // Назначаем события
        menuButton.addEventListener('click', toggleMenu);
        document.addEventListener('click', closeMenu);

        // УЛУЧШЕНИЕ: Предотвращаем закрытие меню при клике внутри самого меню.
        // Это не дает событию "всплыть" до document, который закрывает меню.
        dropdownMenu.addEventListener('click', (event) => {
            event.stopPropagation();
        });
    });
})();
