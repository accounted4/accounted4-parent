<template>

  <AutoComplete
          v-model="symbol"
          @on-search="suggest"
          placeholder="input here"
          style="width:200px"
  >

    <Option v-for="item in suggestions" :value="item.symbol" :key="item.symbol">
      <div>
        <div><b>{{item.symbol}}</b></div>
        <div>{{item.descr}}</div>
      </div>
    </Option>

  </AutoComplete>

</template>



<script>

  import { autoSuggestFinancialInstruments } from '@/js/solrService.js';

  export default {

    data () {
      return {
        symbol: '',
        suggestions: []
      }
    },

    methods: {

      suggest: function(searchTerm) {
        autoSuggestFinancialInstruments(searchTerm).then(restResponse => {
          this.suggestions = restResponse;
        });
      }

    }

  }

</script>


<style scoped>


</style>