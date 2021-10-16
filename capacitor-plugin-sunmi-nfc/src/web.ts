import { WebPlugin } from '@capacitor/core';

import type { SunmiNfcPlugin } from './definitions';

export class SunmiNfcWeb extends WebPlugin implements SunmiNfcPlugin {
  constructor() {
    super({
      name: 'SunmiNfc',
      platforms: ['web'],
    });
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }

  async discoverNfcCard(): Promise<{ results: any }> {
    return {
      results: {
        decId: '1234567890',
        reversedDecId: '3523384905',
        hexId: '49 96 02 D2',
        reversedHexId: 'D2 02 96 49',
      }
    };
  }
}
