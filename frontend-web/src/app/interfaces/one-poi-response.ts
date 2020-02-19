import { Category } from './category';

export interface OnePoiResponse {
    id: string;
    name: string;
    categories: Category[];
    loc: {
        coordinates: number[];
    }
    stars: number;
    qrCode: string;
    uniqueName: string;
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
    coverImage: string;
    images: string[];
    year: number;
    creator?: string;
    status: string;
    schedule: string;
    price?: number;
    averageRating: number;
    userRating: [{
        rating: number;
        poi: string;
        user: string;
        id: string;
    }];
    isRated: boolean;
}
