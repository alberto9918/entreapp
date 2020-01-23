export class PoiCreateDto {
    audioguides: {
        language: {
            language: string;
        }
        originalFile: string;
        translations: [
            {
                id: string,
                translatedFile: string
            }
        ]
    };
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
