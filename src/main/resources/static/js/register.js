(() => {
    const form = document.getElementById('registration-form');
    if (!form) return;

    const showError = (input, message) => {
        const group = input.closest('.form-group');
        group.classList.add('has-error');
        group.querySelector('.error-message').textContent = message;
    };

    const clearError = (input) => {
        const group = input.closest('.form-group');
        group.classList.remove('has-error');
        group.querySelector('.error-message').textContent = '';
    };

    const validators = {
        text(input) {
            const min = +input.dataset.min || 0;
            const max = +input.dataset.max || Infinity;
            if (input.value.trim().length === 0) {
                return input.dataset.msgRequired;
            }
            if (input.value.length < min || input.value.length > max) {
                return input.dataset.msgLength;
            }
        },
        email(input) {
            const value = input.value.trim();
            if (value.length === 0) return input.dataset.msgRequired;

            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(value)) return input.dataset.msgEmail;
        },
        password(input) { return validators.text(input); }
    };

    form.addEventListener('submit', (e) => {
        let valid = true;

        Array.from(form.elements).forEach(el => {
            if (!el.name) return;
            clearError(el);

            const type = el.type === 'password' ? 'password' : el.type;
            const validator = validators[type] || validators.text;
            const error = validator(el);

            if (error) {
                showError(el, error);
                valid = false;
            }
        });

        if (!valid) e.preventDefault();
    });

    form.addEventListener('input', (e) => {
        const el = e.target;
        if (!el.name) return;

        const type = el.type === 'password' ? 'password' : el.type;
        const error = (validators[type] || validators.text)(el);

        if (error) showError(el, error);
        else clearError(el);
    });
})();
