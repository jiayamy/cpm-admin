(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(translationConfig);

    translationConfig.$inject = ['$translateProvider', 'tmhDynamicLocaleProvider'];

    function translationConfig($translateProvider, tmhDynamicLocaleProvider) {
        // Initialize angular-translate
        $translateProvider.useLoader('$translatePartialLoader', {
            urlTemplate: 'i18n/{lang}/{part}.json?v=1.5'
        });

        $translateProvider.preferredLanguage('zh-cn');
        $translateProvider.useStorage('translationStorageProvider');
        $translateProvider.useSanitizeValueStrategy('escaped');
        $translateProvider.addInterpolation('$translateMessageFormatInterpolation');

        tmhDynamicLocaleProvider.localeLocationPattern('i18n/angular-locale_{{locale}}.js?v=1.5');
        tmhDynamicLocaleProvider.useCookieStorage();
        tmhDynamicLocaleProvider.storageKey('NG_TRANSLATE_LANG_KEY');
    }
})();
