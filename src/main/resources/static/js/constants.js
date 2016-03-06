angular.module('Mccy.constants', [])

    .constant('cTimeouts', {
        copiedTooltipHide: 2000,
        resetProgress: 2000
    })

    .constant('cToasterOptions', {
        'time-out': {
            'toast-error': 0,
            'toast-success': 3000
        },
        'close-button': true
    })

    .constant('cBaseVersions',[
        {
            value: 'LATEST',
            label: 'Latest Stable'
        },
        {
            value: 'SNAPSHOT',
            label: 'Snapshot'
        }
    ])

    .constant('cServerTypes',
        {
            // ENV TYPE : 'label'
            VANILLA: 'Official',
            FORGE: 'Forge',
            BUKKIT: 'Bukkit',
            SPIGOT: 'Spigot'
        }
    )

    .constant('cModdedTypes',[
        'FORGE', 'BUKKIT', 'SPIGOT'
    ])

    .constant('cAssetSpecs', {
        WORLD: {
            fileInstructions: 'World save files should end in .zip',
            accept: 'application/zip',
            allowedPatterns: '.zip',
            label: 'world',
            labelPlural: 'worlds'
        }
    })
;