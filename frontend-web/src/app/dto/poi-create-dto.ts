export class PoiCreateDto {
    audioguides: {
        language: {
            language: string;
        }
        originalFile: string;
        translations: [{
            language: {
                language: string;
            },
                translatedFile: string
            }
        ]
    };
    uniqueName:string;
    qrCode: string;
    categories?: string[];
    loc: {
        coordinates: number[];
    }
    creator?: string;
    description: {
        language: {
            language: string;
        }
        originalDescription: string;
        translations: [{
            language: {
                language: string;
            },
            translatedDescription: string
        }]
    };
    name: string;
    coverImage: string;
    images: string[];
    price: number;
    schedule: string;
    status: string;
    year: number;
}
