# capacitor-plugin-sunmi-nfc

This is a capacitor plugin to handle the nfc card

## Install

```bash
npm install capacitor-plugin-sunmi-nfc
npx cap sync
```

## API

<docgen-index>

* [`echo(...)`](#echo)
* [`discoverNfcCard()`](#discovernfccard)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### echo(...)

```typescript
echo(options: { value: string; }) => Promise<{ value: string; }>
```

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ value: string; }</code> |

**Returns:** <code>Promise&lt;{ value: string; }&gt;</code>

--------------------


### discoverNfcCard()

```typescript
discoverNfcCard() => Promise<{ decId: string; reversedDecId: string; hexId: string; reversedHexId: string; }>
```

**Returns:** <code>Promise&lt;{ decId: string; reversedDecId: string; hexId: string; reversedHexId: string; }&gt;</code>

--------------------

</docgen-api>
