<template>

  <div>

    <h1 id='heading'>Debentures</h1>

    <div>
        <Table :columns="tableColumns" :data="debentures"></Table>
    </div>

  </div>

</template>


<script>

import { getDebenturePage } from '@/js/finSecService.js';

export default {

  name: 'debentures',

  data () {
      return {
          tableColumns: [
              {title: 'Symbol',         key: 'symbol'},
              {title: 'Description',    key: 'descr'},
              {title: 'Issued',         key: 'issueDte'},
              {title: 'Maturity',       key: 'maturityDte'},
              {title: 'Rate',           key: 'percentage'},
              {title: 'Effective Rate', key: 'effectiveRate'},
              {title: 'Close',          key: 'closePrice'},
              {title: 'Volume',         key: 'volumeTraded'},
              {title: 'Read Date',      key: 'readDte'},
              {title: 'Underlying Symbol', key: 'underlyingSymbol'},
              {title: 'Underlying Close',  key: 'underlyingClosePrice'},
              {title: 'Underlying Date',   key: 'underlyingReadDte'},
              {title: 'Conversion Price',  key: 'conversionPrice'},
              {title: 'Conversion Rate',   key: 'conversionRate'},
              {title: 'Converted',         key: 'converted'},
              {title: 'Prospectus',        key: 'prospectus'},
              {title: 'Comments',         key: 'comments'}

              // {
              //     title: 'Action',
              //     key: 'action',
              //     width: 100,
              //     align: 'center',
              //     render: (h, params) => {
              //         return h('div', [
              //             h('Button', {
              //                 props: {
              //                     type: 'error',
              //                     size: 'small'
              //                 },
              //                 on: {
              //                     click: () => {
              //                         this.revoke(params.index);
              //                     }
              //                 }
              //             }, 'Revoke')
              //         ]);
              //     }
              // }

          ],
          debentures: []
      }
  },


  mounted: function() {
      this.getDebentures();
  },


  methods: {

    getDebentures: function() {
      getDebenturePage(200, 0).then(restResponse => {
              this.debentures = restResponse.content;
          });
      }


  }


}

</script>


<style scoped>

#heading {
  padding: 30px;
}

</style>
