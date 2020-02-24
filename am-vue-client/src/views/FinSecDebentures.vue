<template>

  <div>

    <h1 id='heading'>Debentures</h1>

    <div>
        <Table
                :columns="tableColumns"
                :data="debentures"
                :no-data-text="noDataMsg"
                :loading="loading"
                :height="600"
        >
            <template slot-scope="{ row, index }" slot="flag">
                <Tooltip max-width="300" :content="row.comments">
                    <Badge :status="row.flag" />
                </Tooltip>
            </template>
        </Table>
    </div>

  </div>

</template>


<script>

import { getDebenturePage } from '@/js/finSecService.js';
import expandRow from './FinSecDebenturesTableSubRow.vue';

export default {

  name: 'debentures',

  data () {
      return {
          tableColumns: [

              // {
              //     type: 'index',
              //     width: 60,
              //     align: 'center'
              // },

              {
                type: 'expand',
                width: 50,
                render: (h, params) => {
                  return h(expandRow, {
                    props: {
                      row: params.row
                    }
                  })
                }
              },


              {title: 'Symbol',  key: 'symbol', width: '120', sortable: true},
              {title: "", slot: 'flag', width: '10'},
              {title: 'Description', key: 'descr', width: '350', sortable: true, resizable: "true" },
              // {title: 'Issued', key: 'issueDte'},
              {title: 'Maturity', key: 'maturityDte', sortable: true, width: '120'},
              //{title: 'Rate',           key: 'percentage'},
              {title: 'Eff Rate %', key: 'effectiveRate', width: '150', sortable: true, align: 'right',
                  filters: [
                      {
                          label: '< 2%',
                          value: 1
                      },
                      {
                          label: '>= 2% AND <=10%',
                          value: 2
                      },
                      {
                          label: '> 10%',
                          value: 3
                      }
                  ],
                  filterMethod (value, row) {
                      if (value === 1) {
                          return row.effectiveRate < 2;
                      } else if (value === 2) {
                          return row.effectiveRate >= 2 && row.effectiveRate <= 10;
                      } else if (value === 3) {
                          return row.effectiveRate > 10;
                      }
                      return false;
                  },
                  render: (h, params) => {
                      return h('span', null != params.row.effectiveRate ? params.row.effectiveRate.toFixed(3) : null);
                  }


              },
              {title: 'Close $',          key: 'closePrice', width: '130', sortable: true, align: 'right',
                  filters: [
                      {
                          label: '< $80',
                          value: 80
                      },
                      {
                          label: '>= $80 AND <= $105',
                          value: 100
                      },
                      {
                          label: '> $105',
                          value: 105
                      }
                  ],
                  filterMethod (value, row) {
                      if (value === 80) {
                          return row.closePrice < 80;
                      } else if (value === 100) {
                          return row.closePrice >= 80 && row.closePrice <= 105;
                      } else if (value === 105) {
                          return row.closePrice > 105;
                      }
                      return false;
                  },

                  render: (h, params) => {
                    return h('span', params.row.closePrice.toFixed(2));
                  }

              },
              {title: 'Volume',         key: 'volumeTraded', width: '120', align: 'right',
                  render: (h, params) => {
                      return h('span', params.row.volumeTraded.toLocaleString());
                  }
              },
              {title: 'Read Date',      key: 'readDte', width: '120'}

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
          debentures: [],
          noDataMsg: "No Data",
          loading: false
      }
  },


  mounted: function() {
      this.getDebentures();
  },


  methods: {

    getDebentures: function() {
      getDebenturePage(200, 0).then(restResponse => {
              this.loading = true;
              this.debentures = restResponse.content;
              this.debentures[1].flag="error";
              this.loading = false;
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
