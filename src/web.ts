import { WebPlugin } from '@capacitor/core';

import type { SunmiNfcPlugin } from './definitions';

export class SunmiNfcWeb extends WebPlugin implements SunmiNfcPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }

  async discoverNfcCard(): Promise<{ decId: string, reversedDecId: string, hexId: string, reversedHexId: string }> {
    return {
      decId: '1234567890',
      reversedDecId: '3523384905',
      hexId: '49 96 02 D2',
      reversedHexId: 'D2 02 96 49',
    };
  }
}
